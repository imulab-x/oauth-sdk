package io.imulab.astrea.sdk.oauth.handler

import io.imulab.astrea.sdk.oauth.request.OAuthAccessRequest
import io.imulab.astrea.sdk.oauth.response.TokenEndpointResponse

/**
 * General interface for handling an access request.
 */
interface AccessRequestHandler {

    /**
     * Update or restore the session so [handleAccessRequest] can decide how to handle the request.
     */
    suspend fun updateSession(request: OAuthAccessRequest)

    /**
     * Handle an access request
     */
    suspend fun handleAccessRequest(request: OAuthAccessRequest, response: TokenEndpointResponse)
}