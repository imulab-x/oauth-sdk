package io.imulab.astrea.sdk.oauth.client.authn

import io.imulab.astrea.sdk.oauth.client.OAuthClient
import io.imulab.astrea.sdk.oauth.request.OAuthRequestForm

/**
 * Provides authentication functions.
 */
interface ClientAuthenticator {

    /**
     * Probe method that returns true if this authenticator can process
     * an authentication method identifier by [method].
     */
    fun supports(method: String): Boolean

    /**
     * Authenticates the client using the provided credentials in the [form].
     * If authentication is successful, returns the [OAuthClient] that just passed
     * authentication. If authentication failed, raise invalid_client error.
     */
    suspend fun authenticate(form: OAuthRequestForm): OAuthClient
}