package io.imulab.astrea.sdk.oauth.error

object TemporarilyUnavailable {
    const val code = "temporarily_unavailable"
    const val status = 503

    val offlineWithName: (String) -> OAuthException =
        { name -> OAuthException(status, code, "$name is temporarily unavailable.") }

    val offline: () -> OAuthException = {
        OAuthException(status, code, "Service is temporarily unavailable.")
    }
}