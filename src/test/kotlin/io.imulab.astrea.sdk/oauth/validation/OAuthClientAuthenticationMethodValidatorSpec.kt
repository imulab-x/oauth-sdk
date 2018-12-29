package io.imulab.astrea.sdk.oauth.validation

import io.imulab.astrea.sdk.`when`
import io.imulab.astrea.sdk.given
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.reserved.AuthenticationMethod
import io.imulab.astrea.sdk.then
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek

object OAuthClientAuthenticationMethodValidatorSpec : Spek({

    given("A client authentication method validator") {
        val validator = OAuthClientAuthenticationMethodValidator

        `when`("authentication method is client_secret_basic") {
            val result = runCatching { validator.validate(AuthenticationMethod.clientSecretBasic) }

            then("validation should pass") {
                assertThat(result.isSuccess).isTrue()
            }
        }

        `when`("authentication method is client_secret_post") {
            val result = runCatching { validator.validate(AuthenticationMethod.clientSecretPost) }

            then("validation should pass") {
                assertThat(result.isSuccess).isTrue()
            }
        }

        `when`("authentication method is other") {
            val result = runCatching { validator.validate("foo") }

            then("validation should fail") {
                assertThat(result.isFailure).isTrue()
                assertThat(result.exceptionOrNull()).isInstanceOf(OAuthException::class.java)
            }
        }
    }
})