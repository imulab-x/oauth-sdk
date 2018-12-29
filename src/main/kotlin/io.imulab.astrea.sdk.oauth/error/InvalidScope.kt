package io.imulab.astrea.sdk.oauth.error

// invalid_scope
// -------------
// The requested scope is invalid, unknown, malformed, or
// exceeds the scope granted by the resource owner.
object InvalidScope {
    const val code = "invalid_scope"
    const val status = 400

    val invalid: (String) -> Throwable =
        { s -> throw OAuthException(status, code, "Scope <$s> is invalid.") }

    val unknown: (String) -> Throwable =
        { s -> throw OAuthException(status, code, "Scope <$s> is unknown.") }

    val malformed: (String) -> Throwable =
        { s -> throw OAuthException(status, code, "Scope <$s> is malformed.") }

    val notGranted: (String) -> Throwable =
        { s -> throw OAuthException(status, code, "Scope <$s> was not granted to client.") }

    val notRequested: (String) -> Throwable =
        { s -> throw OAuthException(status, code, "Scope <$s> was not requested by client.") }
}