package io.imulab.astrea.sdk.oauth.error

// unsupported_grant_type
// ----------------------
// The authorization grant type is not supported by the
// authorization server.
object UnsupportedGrantType {
    const val code = "unsupported_grant_type"
    const val status = 400

    val unsupported: (String) -> Throwable =
        { gt ->
            throw OAuthException(
                status,
                code,
                "Use of grant_type <$gt> is unsupported by this server."
            )
        }
}