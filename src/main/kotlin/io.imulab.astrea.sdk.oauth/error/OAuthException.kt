package io.imulab.astrea.sdk.oauth.error

import io.imulab.astrea.sdk.oauth.reserved.Param
import io.imulab.astrea.sdk.oauth.response.OAuthResponse

class OAuthException(
    override val status: Int,
    val error: String,
    val description: String,
    override val headers: Map<String, String> = emptyMap()
) : RuntimeException("$error: $description"), OAuthResponse {

    override val data: Map<String, String>
        get() = mapOf(
            Param.error to error,
            Param.errorDescription to description
        )
}