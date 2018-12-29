package io.imulab.astrea.sdk.oauth.validation

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.imulab.astrea.sdk.`when`
import io.imulab.astrea.sdk.given
import io.imulab.astrea.sdk.oauth.client.OAuthClient
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.request.OAuthAccessRequest
import io.imulab.astrea.sdk.oauth.reserved.GrantType
import io.imulab.astrea.sdk.then
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek

object OAuthGrantTypeValidatorSpec : Spek({

    given("An oauth grant type validator") {
        val validator = OAuthGrantTypeValidator

        `when`("grant type is authorization_code") {
            val result = runCatching { validator.validate(GrantType.authorizationCode) }

            then("validation should pass") {
                assertThat(result.isSuccess).isTrue()
            }
        }

        `when`("grant type is implicit") {
            val result = runCatching { validator.validate(GrantType.implicit) }

            then("validation should pass") {
                assertThat(result.isSuccess).isTrue()
            }
        }

        `when`("grant type is client_credentials") {
            val result = runCatching { validator.validate(GrantType.clientCredentials) }

            then("validation should pass") {
                assertThat(result.isSuccess).isTrue()
            }
        }

        `when`("grant type is password") {
            val result = runCatching { validator.validate(GrantType.password) }

            then("validation should pass") {
                assertThat(result.isSuccess).isTrue()
            }
        }

        `when`("grant type is refresh_token") {
            val result = runCatching { validator.validate(GrantType.refreshToken) }

            then("validation should pass") {
                assertThat(result.isSuccess).isTrue()
            }
        }

        `when`("grant type is others") {
            val result = runCatching { validator.validate("foo") }

            then("validation should fail") {
                assertThat(result.isFailure).isTrue()
                assertThat(result.exceptionOrNull()).isInstanceOf(OAuthException::class.java)
            }
        }
    }

    given("""
        An oauth grant type validator, and
        a client whose grant type is only client_credentials
    """.trimIndent()) {
        val validator = OAuthGrantTypeValidator
        val client = mock<OAuthClient> {
            onGeneric { grantTypes } doReturn setOf(GrantType.clientCredentials)
        }

        `when`("A request was made by the client with grant_type=client_credentials") {
            val request = OAuthAccessRequest.Builder().also { b ->
                b.client = client
                b.grantTypes = mutableSetOf(GrantType.clientCredentials)
                b.redirectUri = "app://callback"
            }.build()
            val result = runCatching { validator.validate(request) }

            then("validation should pass") {
                assertThat(result.isSuccess).isTrue()
            }
        }

        `when`("A request was made by the client with grant_type=authorization_code") {
            val request = OAuthAccessRequest.Builder().also { b ->
                b.client = client
                b.grantTypes = mutableSetOf(GrantType.authorizationCode)
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