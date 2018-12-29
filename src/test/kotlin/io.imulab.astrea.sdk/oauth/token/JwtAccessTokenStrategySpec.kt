package io.imulab.astrea.sdk.oauth.token

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.imulab.astrea.sdk.`when`
import io.imulab.astrea.sdk.given
import io.imulab.astrea.sdk.oauth.OAuthContext
import io.imulab.astrea.sdk.oauth.client.OAuthClient
import io.imulab.astrea.sdk.oauth.request.OAuthAccessRequest
import io.imulab.astrea.sdk.oauth.request.OAuthRequest
import io.imulab.astrea.sdk.oauth.request.OAuthSession
import io.imulab.astrea.sdk.oauth.reserved.GrantType
import io.imulab.astrea.sdk.oauth.token.JwtAccessTokenStrategySpec.validRequest
import io.imulab.astrea.sdk.oauth.token.strategy.JwtAccessTokenStrategy
import io.imulab.astrea.sdk.then
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.jose4j.jwk.JsonWebKeySet
import org.jose4j.jwk.RsaJwkGenerator
import org.jose4j.jwk.Use
import org.spekframework.spek2.Spek
import java.time.Duration

object JwtAccessTokenStrategySpec : Spek({

    given("A configured strategy") {
        val strategy = Given().strategy

        `when`("asked to generate a token") {
            val request = validRequest()
            val token = runBlocking { strategy.generateToken(request) }

            then("token is not empty") {
                assertThat(token).isNotEmpty()
            }
        }
    }

    given("A configured strategy, and a generated token") {
        val strategy = Given().strategy
        val token = runBlocking { strategy.generateToken(validRequest()) }

        then("should be able to verify token") {
            assertThatCode {
                runBlocking { strategy.verifyToken(token, mock()) }
            }.doesNotThrowAnyException()
        }

        then("should be able to compute identifier") {
            assertThat(strategy.computeIdentifier(token)).isNotEmpty()
        }
    }

}) {
    class Given {
        private val serverJsonWebKeySet = JsonWebKeySet().also { s ->
            s.addJsonWebKey(RsaJwkGenerator.generateJwk(2048).also { k ->
                k.keyId = "FDD2740F-7AD4-4DBA-B35F-9B4638ECCA38"
                k.use = Use.SIGNATURE
                k.algorithm = JwtSigningAlgorithm.RS256.algorithmIdentifier
            })
        }

        private val context = mock<OAuthContext> {
            onGeneric { issuerUrl } doReturn "https://test.com"
            onGeneric { accessTokenLifespan } doReturn Duration.ofMinutes(30)
        }

        val strategy = JwtAccessTokenStrategy(
            oauthContext = context,
            signingAlgorithm = JwtSigningAlgorithm.RS256,
            serverJwks = serverJsonWebKeySet
        )
    }

    fun validRequest(): OAuthRequest {
        val client = mock<OAuthClient> {
            onGeneric { id } doReturn "f52ff96e-3b4c-48bc-8e68-e33f3582e661"
        }
        val session = OAuthSession().also {
            it.subject = "foo"
            it.grantedScopes.add("bar")
        }
        return OAuthAccessRequest.Builder().also {
            it.session = session
            it.client = client
            it.grantTypes = mutableSetOf(GrantType.clientCredentials)
            it.redirectUri = "app://callback"
        }.build()
    }
}