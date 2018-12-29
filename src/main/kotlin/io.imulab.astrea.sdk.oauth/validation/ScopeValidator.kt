package io.imulab.astrea.sdk.oauth.validation

import io.imulab.astrea.sdk.oauth.mustNotMalformedScope
import io.imulab.astrea.sdk.oauth.request.OAuthRequest

/**
 * Validate the parameter `scope`. It must not be malformed according to OAuthConfig spec and
 * it must be allowed by the requesting client.
 */
object ScopeValidator : SpecDefinitionValidator,
    OAuthRequestValidation {
    override fun validate(value: String): String {
        value.mustNotMalformedScope()
        return value
    }

    override fun validate(request: OAuthRequest) {
        request.scopes.forEach { scope ->
            scope.mustNotMalformedScope()
            request.client.mustScope(scope)
        }
    }
}