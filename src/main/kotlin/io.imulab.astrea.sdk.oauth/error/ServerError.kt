package io.imulab.astrea.sdk.oauth.error

// server_error
object ServerError {
    const val code = "server_error"
    const val status = 500

    val wrapped: (Throwable) -> OAuthException =
        { t -> OAuthException(status, code, t.message ?: t.javaClass.name) }

    val internal: (String) -> Throwable =
        { m -> OAuthException(status, code, m) }
}