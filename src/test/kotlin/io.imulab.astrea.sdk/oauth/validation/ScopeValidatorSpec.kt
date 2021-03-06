package io.imulab.astrea.sdk.oauth.validation

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.imulab.astrea.sdk.`when`
import io.imulab.astrea.sdk.given
import io.imulab.astrea.sdk.oauth.client.OAuthClient
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.request.OAuthAuthorizeRequest
import io.imulab.astrea.sdk.oauth.request.OAuthRequest
import io.imulab.astrea.sdk.oauth.validation.ScopeValidatorSpec.requestWithScopes
import io.imulab.astrea.sdk.then
import org.assertj.core.api.Assertions.assertThatCode
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.spekframework.spek2.Spek

object ScopeValidatorSpec : Spek({

    given("a scope validator") {
        val validator = ScopeValidator

        `when`("a request is made with valid scopes") {
            val request = requestWithScopes("foo", "bar")
            then("validation should pass") {
                assertThatCode { validator.validate(request) }
                    .doesNotThrowAnyException()
            }
        }

        `when`("a request is made with malformed scopes") {
            val request = requestWithScopes("foo", "\"bar")
            then("validation should fail") {
                assertThatExceptionOfType(OAuthException::class.java)
                    .isThrownBy { validator.validate(request) }
            }
        }
    }
}) {
    fun requestWithScopes(vararg scopes: String): OAuthRequest {
        val client = mock<OAuthClient> {
            onGeneric { this.scopes } doReturn setOf("foo", "bar")
        }
        return mock<OAuthAuthorizeRequest> {
            onGeneric { this.scopes } doReturn scopes.toSet()
            onGeneric { this.client } doReturn client
        }
    }
}