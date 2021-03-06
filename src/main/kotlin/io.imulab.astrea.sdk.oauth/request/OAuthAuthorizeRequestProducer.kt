package io.imulab.astrea.sdk.oauth.request

import io.imulab.astrea.sdk.oauth.client.ClientLookup
import io.imulab.astrea.sdk.oauth.error.InvalidRequest
import io.imulab.astrea.sdk.oauth.reserved.Param
import io.imulab.astrea.sdk.oauth.reserved.space
import io.imulab.astrea.sdk.oauth.validation.SpecDefinitionValidator

/**
 * Implementation of [OAuthRequestProducer] that takes the input parameter values from [OAuthRequestForm]
 * and populates [OAuthAuthorizeRequest]. This producer also performs some light value based validation
 * to ensure at least specification values are respected. Further validation needs to be performed by
 * validators.
 */
open class OAuthAuthorizeRequestProducer(
    private val lookup: ClientLookup,
    private val responseTypeValidator: SpecDefinitionValidator
) : OAuthRequestProducer {

    override suspend fun produce(form: OAuthRequestForm): OAuthRequest {
        if (form.clientId.isEmpty())
            throw InvalidRequest.required(Param.clientId)

        val client = lookup.find(form.clientId)

        val builder = OAuthAuthorizeRequest.Builder().also { b ->
            b.client = client
            b.redirectUri = client.determineRedirectUri(form.redirectUri)
            b.responseTypes = form.responseType
                .split(space)
                .filter { it.isNotBlank() }
                .toMutableSet()
            b.state = form.state
            b.scopes = form.scope
                .split(space)
                .filter { it.isNotBlank() }
                .toMutableSet()
        }

        return builder.build()
    }
}