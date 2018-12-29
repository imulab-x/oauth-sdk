package io.imulab.astrea.sdk.oauth.request

import java.time.LocalDateTime

open class OAuthSession(
    var subject: String = "",
    var originalRequestId: String = "",
    var originalRequestTime: LocalDateTime? = null,
    var grantedScopes: MutableSet<String> = mutableSetOf(),
    var accessTokenClaims: MutableMap<String, Any> = mutableMapOf()
) {

    open fun merge(another: OAuthSession) {
        if (subject.isEmpty())
            subject = another.subject
        if (originalRequestTime == null)
            originalRequestTime = another.originalRequestTime
        grantedScopes.addAll(another.grantedScopes)
        accessTokenClaims.putAll(another.accessTokenClaims)
    }
}