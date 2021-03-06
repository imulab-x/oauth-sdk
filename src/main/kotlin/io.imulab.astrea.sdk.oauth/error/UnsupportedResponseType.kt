package io.imulab.astrea.sdk.oauth.error

// unsupported_response_type
// -------------------------
// The authorization server does not support obtaining an
// authorization code or access token using this method.
object UnsupportedResponseType {
    const val code = "unsupported_response_type"
    const val status = 400

    val unsupported: (String) -> Throwable =
        { rt ->
            throw OAuthException(
                status,
                code,
                "Use of response_type <$rt> is unsupported by this server."
            )
        }
}