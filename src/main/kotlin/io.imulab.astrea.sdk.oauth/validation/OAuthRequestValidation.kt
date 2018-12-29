package io.imulab.astrea.sdk.oauth.validation

import io.imulab.astrea.sdk.oauth.request.OAuthRequest

/**
 * Rule to validate an [OAuthRequest]. Implementation should raise an error when validation fails; otherwise, should
 * return normally.
 */
interface OAuthRequestValidation {
    fun validate(request: OAuthRequest)
}