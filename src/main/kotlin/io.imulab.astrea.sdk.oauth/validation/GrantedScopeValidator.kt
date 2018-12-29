package io.imulab.astrea.sdk.oauth.validation

import io.imulab.astrea.sdk.oauth.error.InvalidScope
import io.imulab.astrea.sdk.oauth.request.OAuthRequest
import io.imulab.astrea.sdk.oauth.reserved.space

/**
 * Validates the relation between granted scopes and requested scopes. Validation passes when nothing is requested or
 * at least some of the scopes are granted. Validation fails when none of the requested scopes are granted or some of
 * the granted scopes are not requested.
 */
object GrantedScopeValidator : OAuthRequestValidation {

    override fun validate(request: OAuthRequest) {
        if (request.scopes.isEmpty())
            return

        if (request.session.grantedScopes.isEmpty())
            throw InvalidScope.notGranted(request.scopes.joinToString(space))

        val rouge = request.session.grantedScopes.minus(request.scopes)
        if (rouge.isNotEmpty())
            throw InvalidScope.notRequested(rouge.joinToString(space))
    }
}