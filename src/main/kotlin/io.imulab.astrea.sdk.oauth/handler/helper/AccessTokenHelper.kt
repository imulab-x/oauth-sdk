package io.imulab.astrea.sdk.oauth.handler.helper

import io.imulab.astrea.sdk.oauth.OAuthContext
import io.imulab.astrea.sdk.oauth.request.OAuthRequest
import io.imulab.astrea.sdk.oauth.response.AccessTokenResponse
import io.imulab.astrea.sdk.oauth.token.storage.AccessTokenRepository
import io.imulab.astrea.sdk.oauth.token.strategy.AccessTokenStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class AccessTokenHelper(
    private val oauthContext: OAuthContext,
    private val accessTokenStrategy: AccessTokenStrategy,
    private val accessTokenRepository: AccessTokenRepository
) {

    suspend fun createAccessToken(request: OAuthRequest, response: AccessTokenResponse): Job {
        return accessTokenStrategy.generateToken(request).let { accessToken ->
            response.accessToken = accessToken
            response.tokenType = "bearer"
            response.expiresIn = oauthContext.accessTokenLifespan.toMillis() / 60
            withContext(Dispatchers.IO) {
                launch {
                    accessTokenRepository.createAccessTokenSession(accessToken, request)
                }
            }
        }
    }
}