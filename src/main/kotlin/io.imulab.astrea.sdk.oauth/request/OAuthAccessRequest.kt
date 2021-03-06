package io.imulab.astrea.sdk.oauth.request

import io.imulab.astrea.sdk.oauth.client.OAuthClient
import io.imulab.astrea.sdk.oauth.error.InvalidRequest
import io.imulab.astrea.sdk.oauth.reserved.Param

/**
 * An OAuthConfig access request
 */
open class OAuthAccessRequest(
    val grantTypes: Set<String>,
    val code: String,
    val refreshToken: String,
    val redirectUri: String,
    client: OAuthClient,
    scopes: Set<String> = emptySet(),
    session: OAuthSession = OAuthSession()
) : OAuthRequest(client = client, scopes = scopes, session = session) {

    class Builder(
        var grantTypes: MutableSet<String> = mutableSetOf(),
        var code: String = "",
        var refreshToken: String = "",
        var redirectUri: String = "",
        var scopes: MutableSet<String> = mutableSetOf(),
        var client: OAuthClient? = null,
        var session: OAuthSession = OAuthSession()
    ) {

        fun build(): OAuthAccessRequest {
            if (grantTypes.isEmpty())
                throw InvalidRequest.required(Param.grantType)

            checkNotNull(client)

            return OAuthAccessRequest(
                grantTypes = grantTypes.toSet(),
                code = code,
                refreshToken = refreshToken,
                scopes = scopes,
                redirectUri = redirectUri,
                client = client!!,
                session = session
            )
        }
    }
}