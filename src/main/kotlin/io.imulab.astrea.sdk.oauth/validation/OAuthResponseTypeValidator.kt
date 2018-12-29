package io.imulab.astrea.sdk.oauth.validation

import io.imulab.astrea.sdk.oauth.assertType
import io.imulab.astrea.sdk.oauth.error.UnauthorizedClient
import io.imulab.astrea.sdk.oauth.error.UnsupportedResponseType
import io.imulab.astrea.sdk.oauth.request.OAuthAuthorizeRequest
import io.imulab.astrea.sdk.oauth.request.OAuthRequest
import io.imulab.astrea.sdk.oauth.reserved.ResponseType

/**
 * Validates the set relation: `response_type = {code, token}`.
 * When in the context of a request, it must be registered/allowed by the client.
 */
object OAuthResponseTypeValidator : SpecDefinitionValidator,
    OAuthRequestValidation {
    override fun validate(value: String): String {
        return when (value) {
            ResponseType.code, ResponseType.token -> value
            else -> throw UnsupportedResponseType.unsupported(value)
        }
    }

    override fun validate(request: OAuthRequest) {
        val ar = request.assertType<OAuthAuthorizeRequest>()
        ar.responseTypes.forEach {
            validate(it)
            if (!ar.client.responseTypes.contains(it))
                throw UnauthorizedClient.forbiddenResponseType(it)
        }
    }
}