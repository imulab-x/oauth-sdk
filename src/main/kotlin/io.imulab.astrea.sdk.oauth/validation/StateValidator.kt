package io.imulab.astrea.sdk.oauth.validation

import io.imulab.astrea.sdk.oauth.OAuthContext
import io.imulab.astrea.sdk.oauth.assertType
import io.imulab.astrea.sdk.oauth.error.InvalidRequest
import io.imulab.astrea.sdk.oauth.request.OAuthAuthorizeRequest
import io.imulab.astrea.sdk.oauth.request.OAuthRequest

/**
 * Validate the parameter `state`. Its entropy must not be less than [OAuthContext.stateEntropy]. Because this is an
 * optional parameter, empty string is allowed.
 */
class StateValidator(private val oauthContext: OAuthContext) :
    OAuthRequestValidation {
    override fun validate(request: OAuthRequest) {
        val l = request.assertType<OAuthAuthorizeRequest>().state.length
        if (l in 1..(oauthContext.stateEntropy - 1))
            throw InvalidRequest.unmet("<state> length must not be less than ${oauthContext.stateEntropy}")
    }
}