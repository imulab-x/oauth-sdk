package io.imulab.astrea.sdk.oauth.client

import com.nhaarman.mockitokotlin2.argThat
import com.nhaarman.mockitokotlin2.doAnswer
import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.imulab.astrea.sdk.`when`
import io.imulab.astrea.sdk.given
import io.imulab.astrea.sdk.oauth.client.ClientSecretBasicAuthenticatorSpec.requestWithCredential
import io.imulab.astrea.sdk.oauth.client.authn.ClientSecretBasicAuthenticator
import io.imulab.astrea.sdk.oauth.client.pwd.BCryptPasswordEncoder
import io.imulab.astrea.sdk.oauth.error.InvalidClient
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.request.OAuthRequestForm
import io.imulab.astrea.sdk.oauth.reserved.AuthenticationMethod
import io.imulab.astrea.sdk.oauth.reserved.Header
import io.imulab.astrea.sdk.then
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatExceptionOfType
import org.spekframework.spek2.Spek
import java.util.*

object ClientSecretBasicAuthenticatorSpec : Spek({

    given("a properly configured authenticator") {
        val authenticator = Given().authenticator

        then("should support only client_secret_basic") {
            assertThat(authenticator.supports(AuthenticationMethod.clientSecretBasic)).isTrue()
            assertThat(authenticator.supports(AuthenticationMethod.clientSecretPost)).isFalse()
        }

        `when`("a request is made with good credential") {
            val request = requestWithCredential("foo", "s3cret")

            then("client authentication should pass and return client") {
                runBlocking {
                    assertThat(authenticator.authenticate(request)).extracting {
                        it.id
                    }.isEqualTo("foo")
                }
            }
        }

        `when`("a request is made with bad credential") {
            val request = requestWithCredential("foo", "invalid")

            then("client authentication should raise error") {
                assertThatExceptionOfType(OAuthException::class.java)
                    .isThrownBy { runBlocking { authenticator.authenticate(request) } }
            }
        }

        `when`("a request is made with unknown client") {
            val request = requestWithCredential("bar", "s3cret")

            then("client authentication should raise error") {
                assertThatExceptionOfType(OAuthException::class.java)
                    .isThrownBy { runBlocking { authenticator.authenticate(request) } }
            }
        }

        `when`("a request is made with empty credentials") {
            val request = requestWithCredential("", "")

            then("client authentication should raise error") {
                assertThatExceptionOfType(OAuthException::class.java)
                    .isThrownBy { runBlocking { authenticator.authenticate(request) } }
            }
        }

        `when`("a request is made with without authorization header") {
            val request = OAuthRequestForm(
                httpForm = mutableMapOf()
            )

            then("client authentication should raise error") {
                assertThatExceptionOfType(OAuthException::class.java)
                    .isThrownBy { runBlocking { authenticator.authenticate(request) } }
            }
        }

        `when`("a request is made with without non-basic authorization header") {
            val request = OAuthRequestForm(mutableMapOf(Header.authorization to listOf("Bearer foo")))

            then("client authentication should raise error") {
                assertThatExceptionOfType(OAuthException::class.java)
                    .isThrownBy { runBlocking { authenticator.authenticate(request) } }
            }
        }
    }

}) {

    class Given {
        private val encoder = BCryptPasswordEncoder()
        private val client = mock<OAuthClient> {
            onGeneric { it.id } doReturn "foo"
            onGeneric { it.secret } doReturn encoder.encode("s3cret").toByteArray()
        }
        private val clientLookup = mock<ClientLookup> {
            onBlocking { find("foo") } doReturn client
            onBlocking { find(argThat { this != "foo" }) } doAnswer { throw InvalidClient.unknown() }
        }
        val authenticator = ClientSecretBasicAuthenticator(clientLookup, encoder)
    }

    fun requestWithCredential(username: String, secret: String): OAuthRequestForm {
        return OAuthRequestForm(
            httpForm = mutableMapOf(
                Header.authorization to listOf(
                    "Basic " + Base64.getEncoder().encodeToString("$username:$secret".toByteArray())
                )
            )
        )
    }
}