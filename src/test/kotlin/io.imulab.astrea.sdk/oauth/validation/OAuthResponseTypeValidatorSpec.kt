package io.imulab.astrea.sdk.oauth.validation

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.imulab.astrea.sdk.`when`
import io.imulab.astrea.sdk.given
import io.imulab.astrea.sdk.oauth.client.OAuthClient
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.request.OAuthAuthorizeRequest
import io.imulab.astrea.sdk.oauth.reserved.ResponseType
import io.imulab.astrea.sdk.then
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek

object OAuthResponseTypeValidatorSpec : Spek({

    given("An oauth response type validator") {
        val validator = OAuthResponseTypeValidator

        `when`("response type is code") {
            val result = runCatching { validator.validate(ResponseType.code) }

            then("validation should pass") {
                assertThat(result.isSuccess).isTrue()
            }
        }

        `when`("response type is token") {
            val result = runCatching { validator.validate(ResponseType.token) }

            then("validation should pass") {
                assertThat(result.isSuccess).isTrue()
            }
        }

        `when`("response type type is others") {
            val result = runCatching { validator.validate("foo") }

            then("validation should fail") {
                assertThat(result.isFailure).isTrue()
                assertThat(result.exceptionOrNull()).isInstanceOf(OAuthException::class.java)
            }
        }
    }

    given("""
        An oauth response type validator, and
        a client whose response type is only token
    """.trimIndent()) {
        val validator = OAuthResponseTypeValidator
        val client = mock<OAuthClient> {
            onGeneric { responseTypes } doReturn setOf(ResponseType.token)
        }

        `when`("A request was made by the client with response_type=token") {
            val request = OAuthAuthorizeRequest.Builder().also { b ->
                b.client = client
                b.responseTypes = mutableSetOf(ResponseType.token)
                b.redirectUri = "app://callback"
            }.build()
            val result = runCatching { validator.validate(request) }

            then("validation should pass") {
                assertThat(result.isSuccess).isTrue()
            }
        }

        `when`("A request was made by the client with response_type=code") {
            val request = OAuthAuthorizeRequest.Builder().also { b ->
                b.client = client
                b.responseTypes = mutableSetOf(ResponseType.code)
                b.redirectUri = "app://callback"
            }.build()
            val result = runCatching { validator.validate(request) }

            then("validation should fail") {
                assertThat(result.isFailure).isTrue()
                assertThat(result.exceptionOrNull()).isInstanceOf(OAuthException::class.java)
            }
        }
    }
})