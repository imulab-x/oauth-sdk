package io.imulab.astrea.sdk.oauth.client

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.imulab.astrea.sdk.`when`
import io.imulab.astrea.sdk.given
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.reserved.GrantType
import io.imulab.astrea.sdk.then
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek

object OAuthClientSpec : Spek({

    given("A client whose grant_type is only client_credentials") {
        val client = GivenClient(mock {
            onGeneric { grantTypes } doReturn setOf(GrantType.clientCredentials)
        })

        `when`("granting type client_credentials") {
            val result = runCatching { client.mustGrantType(GrantType.clientCredentials) }

            then("should return without error") {
                assertThat(result.isSuccess).isTrue()
                assertThat(result.getOrNull()).isEqualTo(GrantType.clientCredentials)
            }
        }

        `when`("granting type authorization_code") {
            val result = runCatching { client.mustGrantType(GrantType.authorizationCode) }

            then("should raise error") {
                assertThat(result.isFailure).isTrue()
                assertThat(result.exceptionOrNull()).isInstanceOf(OAuthException::class.java)
            }
        }
    }

    given("A client whose scope is only foo") {
        val client = GivenClient(mock {
            onGeneric { scopes } doReturn setOf("foo")
        })

        `when`("asking for scope foo") {
            val result = runCatching { client.mustScope("foo") }

            then("should return without error") {
                assertThat(result.isSuccess).isTrue()
                assertThat(result.getOrNull()).isEqualTo("foo")
            }
        }

        `when`("asking for scope bar") {
            val result = runCatching { client.mustScope("bar") }

            then("should raise error") {
                assertThat(result.isFailure).isTrue()
                assertThat(result.exceptionOrNull()).isInstanceOf(OAuthException::class.java)
            }
        }
    }

    given("A client who has one registered redirect_uri") {
        val client = GivenClient(mock {
            onGeneric { redirectUris } doReturn setOf("app://callback")
        })

        `when`("presented with no uri") {
            val result = runCatching { client.determineRedirectUri("") }

            then("should return the registered uri") {
                assertThat(result.isSuccess).isTrue()
                assertThat(result.getOrNull()).isEqualTo("app://callback")
            }
        }

        `when`("presented with the same uri as registered") {
            val result = runCatching { client.determineRedirectUri("app://callback") }

            then("should return the registered uri") {
                assertThat(result.isSuccess).isTrue()
                assertThat(result.getOrNull()).isEqualTo("app://callback")
            }
        }

        `when`("presented with the a different uri") {
            val result = runCatching { client.determineRedirectUri("foo://callback") }

            then("should raise error") {
                assertThat(result.isFailure).isTrue()
                assertThat(result.exceptionOrNull()).isInstanceOf(OAuthException::class.java)
            }
        }
    }

    given("A client who has multiple registered redirect_uri") {
        val client = GivenClient(mock {
            onGeneric { redirectUris } doReturn setOf("app://callback", "app://callback2")
        })

        `when`("presented with no uri") {
            val result = runCatching { client.determineRedirectUri("") }

            then("should raise error") {
                assertThat(result.isFailure).isTrue()
                assertThat(result.exceptionOrNull()).isInstanceOf(OAuthException::class.java)
            }
        }

        `when`("presented with the same uri as registered") {
            val result = runCatching { client.determineRedirectUri("app://callback") }

            then("should return the registered uri") {
                assertThat(result.isSuccess).isTrue()
                assertThat(result.getOrNull()).isEqualTo("app://callback")
            }
        }

        `when`("presented with the a different uri") {
            val result = runCatching { client.determineRedirectUri("foo://callback") }

            then("should raise error") {
                assertThat(result.isFailure).isTrue()
                assertThat(result.exceptionOrNull()).isInstanceOf(OAuthException::class.java)
            }
        }
    }
}) {

    /**
     * A client implementation whose property access is backed by a mocked instance. Because
     * mockito cannot invoke abstract method (i.e. default implementation on an interface) on
     * mocked instances, we have to make a concrete implementation.
     */
    class GivenClient(client: OAuthClient = mock()) : OAuthClient {
        override val id: String = client.id
        override val secret: ByteArray = client.secret
        override val name: String = client.name
        override val type: String = client.type
        override val redirectUris: Set<String> = client.redirectUris
        override val responseTypes: Set<String> = client.responseTypes
        override val grantTypes: Set<String> = client.grantTypes
        override val scopes: Set<String> = client.scopes
    }
}