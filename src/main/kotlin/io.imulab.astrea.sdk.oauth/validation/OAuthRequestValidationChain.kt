package io.imulab.astrea.sdk.oauth.validation

import io.imulab.astrea.sdk.oauth.request.OAuthRequest

/**
 * Validation container which delegates work to [validators] one by one.
 */
class OAuthRequestValidationChain(
    private val validators: List<OAuthRequestValidation>
) : OAuthRequestValidation {
    override fun validate(request: OAuthRequest) {
        validators.forEach { it.validate(request) }
    }
}