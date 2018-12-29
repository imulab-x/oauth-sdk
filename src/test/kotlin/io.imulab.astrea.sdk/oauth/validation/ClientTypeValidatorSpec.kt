package io.imulab.astrea.sdk.oauth.validation

import io.imulab.astrea.sdk.`when`
import io.imulab.astrea.sdk.given
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.reserved.ClientType
import io.imulab.astrea.sdk.then
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek

object ClientTypeValidatorSpec : Spek({
    given("A client type validator") {
        val validator = ClientTypeValidator

        `when`("validating client_type=public") {
            val result = runCatching { validator.validate(ClientType.public) }

            then("validation should pass") {
                assertThat(result.isSuccess).isTrue()
            }
        }

        `when`("validating client_type=confidential") {
            val result = runCatching { validator.validate(ClientType.confidential) }

            then("validation should pass") {
                assertThat(result.isSuccess).isTrue()
            }
        }

        `when`("validating client_type is others") {
            val result = runCatching { validator.validate("foo") }

            then("validation should fail") {
                assertThat(result.isFailure).isTrue()
                assertThat(result.exceptionOrNull()).isInstanceOf(OAuthException::class.java)
            }
        }
    }
})