package io.imulab.astrea.sdk.oauth.handler

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.imulab.astrea.sdk.`when`
import io.imulab.astrea.sdk.given
import io.imulab.astrea.sdk.oauth.OAuthContext
import io.imulab.astrea.sdk.oauth.client.OAuthClient
import io.imulab.astrea.sdk.oauth.error.OAuthException
import io.imulab.astrea.sdk.oauth.handler.OAuthClientCredentialsHandlerSpec.validAccessRequest
import io.imulab.astrea.sdk.oauth.handler.OAuthClientCredentialsHandlerSpec.validAccessRequestByPublicClient
import io.imulab.astrea.sdk.oauth.handler.helper.AccessTokenHelper
import io.imulab.astrea.sdk.oauth.handler.helper.RefreshTokenHelper
import io.imulab.astrea.sdk.oauth.request.OAuthAccessRequest
import io.imulab.astrea.sdk.oauth.reserved.ClientType
import io.imulab.astrea.sdk.oauth.reserved.GrantType
import io.imulab.astrea.sdk.oauth.response.TokenEndpointResponse
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import io.imulab.astrea.sdk.oauth.token.storage.MemoryAccessTokenRepository
import io.imulab.astrea.sdk.oauth.token.storage.MemoryRefreshTokenRepository
import io.imulab.astrea.sdk.oauth.token.strategy.HmacSha2RefreshTokenStrategy
import io.imulab.astrea.sdk.oauth.token.strategy.JwtAccessTokenStrategy
import io.imulab.astrea.sdk.then
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.jose4j.jwk.JsonWebKeySet
import org.jose4j.jwk.RsaJwkGenerator
import org.jose4j.jwk.Use
import org.jose4j.keys.AesKey
import org.spekframework.spek2.Spek
import java.time.Duration

object OAuthClientCredentialsHandlerSpec : Spek({

    given("a configured handler") {
        val given = Given()
        val handler = given.handler

        `when`("a valid request is made") {
            val request = validAccessRequest()
            val response = TokenEndpointResponse()
            runBlocking {
                handler.updateSession(request)
                handler.handleAccessRequest(request, response)
            }

            then("an access token should have been issued in response") {
                assertThat(response.accessToken).isNotEmpty()
            }

            then("a refresh token should have been issued in response") {
                assertThat(response.refreshToken).isNotEmpty()
            }

            then("token type should have been set in response") {
                assertThat(response.tokenType).isEqualTo("bearer")
            }

            then("expiration ttl should have been set in response") {
                assertThat(response.expiresIn).isGreaterThan(0)
            }

            then("granted scopes should have been set in response") {
                assertThat(response.scope).contains("foo", "bar")
            }

            then("an access token session should have been created") {
                assertThat(runBlocking { given.accessTokenRepository.getAccessTokenSession(response.accessToken) })
                    .isNotNull
            }
        }

        `when`("a request with grant_type<>client_credentials is made") {
            val client = mock<OAuthClient> { onGeneric { id } doReturn "foo" }
            val request = OAuthAccessRequest.Builder().also { b ->
                b.client = client
                b.scopes = mutableSetOf("foo", "bar", "offline_access")
                b.grantTypes = mutableSetOf(GrantType.authorizationCode)
                b.redirectUri = "https://test.com/callback"
            }.build()
            val response = TokenEndpointResponse()
            runBlocking {
                handler.updateSession(request)
                handler.handleAccessRequest(request, response)
            }

            then("nothing should be handled") {
                assertThat(response.accessToken).isEmpty()
            }
        }

        `when`("a request is made by a public client") {
            val request = validAccessRequestByPublicClient()
            val response = TokenEndpointResponse()
            val result = runCatching {
                runBlocking {
                    handler.updateSession(request)
                    handler.handleAccessRequest(request, response)
                }
            }

            then("an error should have been raised") {
                assertThat(result.isFailure).isTrue()
                assertThat(result.exceptionOrNull()).isInstanceOf(OAuthException::class.java)
            }
        }
    }
}) {

    class Given {

        private val oauthContext = mock<OAuthContext> {
            onGeneric { accessTokenLifespan } doReturn Duration.ofMinutes(10)
        }

        private val serverJwks = JsonWebKeySet().also { s ->
            s.addJsonWebKey(RsaJwkGenerator.generateJwk(2048).also { k ->
                k.use = Use.SIGNATURE
                k.keyId = "20474f4a-f49d-4e29-bc65-312eed593215"
                k.algorithm = JwtSigningAlgorithm.RS256.algorithmIdentifier
            })
        }

        private val accessTokenStrategy = JwtAccessTokenStrategy(
            oauthContext = oauthContext,
            signingAlgorithm = JwtSigningAlgorithm.RS256,
            serverJwks = serverJwks
        )

        val accessTokenRepository = MemoryAccessTokenRepository()

        private val refreshTokenStrategy = HmacSha2RefreshTokenStrategy(
            key = AesKey("2fb9a6e7a6014e609cc7a42812dae6c7".toByteArray()),
            tokenLength = 16,
            signingAlgorithm = JwtSigningAlgorithm.HS256
        )

        private val refreshTokenRepository = MemoryRefreshTokenRepository()

        val handler = OAuthClientCredentialsHandler(
            refreshTokenHelper = RefreshTokenHelper(
                refreshTokenStrategy = refreshTokenStrategy,
                refreshTokenRepository = refreshTokenRepository
            ),
            accessTokenHelper = AccessTokenHelper(
                oauthContext = oauthContext,
                accessTokenRepository = accessTokenRepository,
                accessTokenStrategy = accessTokenStrategy
            )
        )
    }

    fun validAccessRequest(): OAuthAccessRequest {
        val client = mock<OAuthClient> {
            onGeneric { id } doReturn "foo"
        }
        return OAuthAccessRequest.Builder().also { b ->
            b.client = client
            b.scopes = mutableSetOf("foo", "bar", "offline_access")
            b.grantTypes = mutableSetOf(GrantType.clientCredentials)
            b.redirectUri = "https://test.com/callback"
        }.build()
    }

    fun validAccessRequestByPublicClient(): OAuthAccessRequest {
        val client = mock<OAuthClient> {
            onGeneric { id } doReturn "foo"
            onGeneric { type } doReturn ClientType.public
        }
        return OAuthAccessRequest.Builder().also { b ->
            b.client = client
            b.scopes = mutableSetOf("foo", "bar")
            b.grantTypes = mutableSetOf(GrantType.clientCredentials)
            b.redirectUri = "https://test.com/callback"
        }.build()
    }
}