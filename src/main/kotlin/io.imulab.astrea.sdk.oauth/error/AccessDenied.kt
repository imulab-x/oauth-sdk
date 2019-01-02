package io.imulab.astrea.sdk.oauth.error

// access_denied
// -------------
// The resource owner or authorization server denied the
// request.
object AccessDenied {
    const val code = "access_denied"
    const val status = 403

    val byOwner: () -> Throwable =
        { OAuthException(status, code, "Resource owner denied the request.") }

    val byServer: (String) -> Throwable =
        { reason ->
            OAuthException(
                status,
                code,
                "Resource server denied the request. $reason".trim()
            )
        }

    val noAuthenticationOnNonePrompt: () -> Throwable =
        {
            OAuthException(
                status,
                code,
                "Server cannot resolve authentication, and <none> prompt is requested."
            )
        }

    val noAuthorizationOnNonePrompt: () -> Throwable =
        {
            OAuthException(
                status,
                code,
                "Server cannot resolve user authorization, and <none> prompt is requested."
            )
        }

    val newAuthenticationOnNonePrompt: () -> Throwable =
        {
            OAuthException(
                status,
                code,
                "New authentication took place (<none> prompt requested but <auth_time> is after request time)."
            )
        }

    val oldAuthenticationOnLoginPrompt: () -> Throwable =
        {
            OAuthException(
                status,
                code,
                "Authentication did not happen (<login> prompt requested but <auth_time> is still before original request time)."
            )
        }

    val authenticationExpired: () -> Throwable =
        {
            OAuthException(
                status,
                code,
                "Authentication expired (<auth_time> happened longer ago than <max_age>)."
            )
        }
}