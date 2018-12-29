package io.imulab.astrea.sdk.oauth.request

import io.imulab.astrea.sdk.oauth.client.OAuthClient
import java.time.LocalDateTime
import java.util.*

/**
 * Super class of all OAuthConfig requests.
 */
open class OAuthRequest(
    var id: String = UUID.randomUUID().toString(),
    var requestTime: LocalDateTime = LocalDateTime.now(),
    val client: OAuthClient,
    val scopes: Set<String>,
    val session: OAuthSession = OAuthSession()
) {
    /**
     * Convenience method to grant a scope. The granted scope must be in the requested [scopes].
     */
    fun grantScope(scope: String) {
        if (scopes.contains(scope))
            session.grantedScopes.add(scope)
    }
}