package io.imulab.astrea.sdk.oauth.reserved

object ResponseType {
    const val code = "code"
    const val token = "token"

    enum class Value(val spec: String) {
        Code(code), Token(token)
    }
}