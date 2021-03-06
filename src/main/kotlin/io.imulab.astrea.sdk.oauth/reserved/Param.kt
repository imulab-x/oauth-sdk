package io.imulab.astrea.sdk.oauth.reserved

object Param {
    // (new to) client
    const val clientId = "client_id"
    const val clientSecret = "client_secret"

    // (new to) authorization request
    const val responseType = "response_type"
    const val redirectUri = "redirect_uri"
    const val scope = "scope"
    const val state = "state"

    // (new to) authorization response
    const val code = "code"

    // (new to) access token request
    const val grantType = "grant_type"
    const val username = "username"
    const val password = "password"

    // (new to) access token response
    const val accessToken = "access_token"
    const val tokenType = "token_type"
    const val expiresIn = "expires_in"
    const val refreshToken = "refresh_token"

    // (new to) error
    const val error = "error"
    const val errorDescription = "error_description"
    const val errorUri = "error_uri"

    object Internal {
        const val loginToken = "login_token"
        const val consentToken = "consent_token"
    }
}