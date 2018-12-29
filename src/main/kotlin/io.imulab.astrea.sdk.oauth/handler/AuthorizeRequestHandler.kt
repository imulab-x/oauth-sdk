package io.imulab.astrea.sdk.oauth.handler

import io.imulab.astrea.sdk.oauth.request.OAuthAuthorizeRequest
import io.imulab.astrea.sdk.oauth.response.AuthorizeEndpointResponse

/**
 * General interface for handling an authorization request.
 */
interface AuthorizeRequestHandler {

    /**
     * Handle an authorization request.
     */
    suspend fun handleAuthorizeRequest(request: OAuthAuthorizeRequest, response: AuthorizeEndpointResponse)
}