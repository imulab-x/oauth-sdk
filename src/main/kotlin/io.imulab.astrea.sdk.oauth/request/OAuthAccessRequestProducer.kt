package io.imulab.astrea.sdk.oauth.request

import io.imulab.astrea.sdk.oauth.client.authn.ClientAuthenticators
import io.imulab.astrea.sdk.oauth.error.InvalidRequest
import io.imulab.astrea.sdk.oauth.reserved.Param
import io.imulab.astrea.sdk.oauth.reserved.space
import io.imulab.astrea.sdk.oauth.validation.ScopeValidator
import io.imulab.astrea.sdk.oauth.validation.SpecDefinitionValidator

/**
 * Implementation of [OAuthRequestProducer] that takes the input parameter values from [OAuthRequestForm]
 * and populates [OAuthAccessRequest].
 *
 * This producer is responsible for authenticating the client using [ClientAuthenticators].
 *
 * This producer also performs some light value based validation to ensure at least specification values
 * are respected. Further validation needs to be performed by validators.
 */
open class OAuthAccessRequestProducer(
    private val grantTypeValidator: SpecDefinitionValidator,
    private val clientAuthenticators: ClientAuthenticators
) : OAuthRequestProducer {

    protected open suspend fun builder(form: OAuthRequestForm): OAuthAccessRequest.Builder {
        if (form.clientId.isEmpty())
            throw InvalidRequest.required(Param.clientId)

        val client = clientAuthenticators.authenticate(form)

        return OAuthAccessRequest.Builder().also { b ->
            b.client = client
            b.code = form.code
            b.refreshToken = form.refreshToken
            b.grantTypes = form.grantType
                .split(space)
                .filter { it.isNotBlank() }
                .map { grantTypeValidator.validate(it) }
                .toMutableSet()
            b.scopes = form.scope
                .split(space)
                .filter { it.isNotBlank() }
                .map { ScopeValidator.validate(it) }
                .toMutableSet()
            b.redirectUri = form.redirectUri
        }
    }

    override suspend fun produce(form: OAuthRequestForm): OAuthRequest {
        return builder(form).build()
    }
}