package io.imulab.astrea.sdk.oauth.handler.helper

import io.imulab.astrea.sdk.oauth.request.OAuthAccessRequest
import io.imulab.astrea.sdk.oauth.response.TokenEndpointResponse
import io.imulab.astrea.sdk.oauth.token.storage.RefreshTokenRepository
import io.imulab.astrea.sdk.oauth.token.strategy.RefreshTokenStrategy
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class RefreshTokenHelper(
    private val refreshTokenStrategy: RefreshTokenStrategy,
    private val refreshTokenRepository: RefreshTokenRepository
) {

    suspend fun createRefreshToken(request: OAuthAccessRequest, response: TokenEndpointResponse): Job {
        return refreshTokenStrategy.generateToken(request).let { refreshToken ->
            response.refreshToken = refreshToken
            withContext(Dispatchers.IO) {
                launch {
                    refreshTokenRepository.createRefreshTokenSession(refreshToken, request)
                }
            }
        }
    }
}