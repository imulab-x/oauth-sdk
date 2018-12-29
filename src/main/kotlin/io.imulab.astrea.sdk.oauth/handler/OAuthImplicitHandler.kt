package io.imulab.astrea.sdk.oauth.handler

import io.imulab.astrea.sdk.oauth.OAuthContext
import io.imulab.astrea.sdk.oauth.exactly
import io.imulab.astrea.sdk.oauth.request.OAuthAuthorizeRequest
import io.imulab.astrea.sdk.oauth.reserved.GrantType
import io.imulab.astrea.sdk.oauth.reserved.ResponseType
import io.imulab.astrea.sdk.oauth.reserved.StandardScope
import io.imulab.astrea.sdk.oauth.response.AuthorizeEndpointResponse
import io.imulab.astrea.sdk.oauth.token.storage.AccessTokenRepository
import io.imulab.astrea.sdk.oauth.token.strategy.AccessTokenStrategy

class OAuthImplicitHandler(
    private val oauthContext: OAuthContext,
    private val accessTokenStrategy: AccessTokenStrategy,
    private val accessTokenRepository: AccessTokenRepository
) : AuthorizeRequestHandler {

    override suspend fun handleAuthorizeRequest(request: OAuthAuthorizeRequest, response: AuthorizeEndpointResponse) {
        if (!request.responseTypes.exactly(ResponseType.token))
            return

        request.client.mustGrantType(GrantType.implicit)

        response.scope = request.session.grantedScopes.apply { remove(StandardScope.offlineAccess) }
        response.state = request.state

        accessTokenStrategy.generateToken(request).also { accessToken ->
            accessTokenRepository.createAccessTokenSession(accessToken, request)
            response.accessToken = accessToken
            response.tokenType = "bearer"
            response.expiresIn = oauthContext.accessTokenLifespan.toMillis() / 60
        }

        response.handledResponseTypes.add(ResponseType.token)
    }
}