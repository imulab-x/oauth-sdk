package io.imulab.astrea.sdk.oauth.validation

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.imulab.astrea.sdk.`when`
import io.imulab.astrea.sdk.given
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.request.OAuthAuthorizeRequest
import io.imulab.astrea.sdk.oauth.request.OAuthRequest
import io.imulab.astrea.sdk.oauth.validation.StateValidatorSpec.requestWithState
import io.imulab.astrea.sdk.then
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.spekframework.spek2.Spek

object StateValidatorSpec : Spek({

    given("a state validator with configured state entropy") {
        val validator = StateValidator(
            mock { onGeneric { stateEntropy } doReturn 8 }
        )

        `when`("a request is made with sufficient state entropy") {
            val request = requestWithState("12345678")
            then("validation should pass") {
                assertThatCode { validator.validate(request) }
                    .doesNotThrowAnyException()
            }
        }

        `when`("a request is made with no state") {
            val request = requestWithState("")
            then("validation should pass, because state is optional") {
                assertThatCode { validator.validate(request) }
                    .doesNotThrowAnyException()
            }
        }

        `when`("a request is made with insufficient state entropy") {
            val request = requestWithState("123")
            then("validation should fail") {
                assertThatExceptionOfType(OAuthException::class.java)
                    .isThrownBy { validator.validate(request) }
            }
        }
    }
}) {
    fun requestWithState(v: String): OAuthRequest {
        return mock<OAuthAuthorizeRequest> {
            onGeneric { state } doReturn v
        }
    }
}