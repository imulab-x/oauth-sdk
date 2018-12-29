package io.imulab.astrea.sdk.oauth.token

import com.nhaarman.mockitokotlin2.mock
import io.imulab.astrea.sdk.`when`
import io.imulab.astrea.sdk.given
import io.imulab.astrea.sdk.oauth.token.strategy.HmacSha2RefreshTokenStrategy
import io.imulab.astrea.sdk.then
import kotlinx.coroutines.runBlocking
import org.assertj.core.api.Assertions.assertThat
import org.assertj.core.api.Assertions.assertThatCode
import org.jose4j.keys.AesKey
import org.spekframework.spek2.Spek

object HmacSha2RefreshTokenStrategySpec : Spek({
    given("A configured strategy") {
        val strategy = HmacSha2RefreshTokenStrategy(
            key = AesKey("803b7e722dc04a8a871eec1a82676f23".toByteArray()),
            signingAlgorithm = JwtSigningAlgorithm.HS256
        )

        `when`("asked to generate a token") {
            val token = runBlocking { strategy.generateToken(mock()) }

            then("should have generate a token") {
                assertThat(token).isNotEmpty()
            }

            then("should be able to verify the same token") {
                assertThatCode { runBlocking { strategy.verifyToken(token, mock()) } }.doesNotThrowAnyException()
            }

            then("should be able to compute identifier") {
                assertThat(strategy.computeIdentifier(token)).isNotEmpty()
            }
        }
    }

    given("Some signing algorithm name") {
        val hmac = listOf(JwtSigningAlgorithm.HS256, JwtSigningAlgorithm.HS384, JwtSigningAlgorithm.HS512)
        val rsa = listOf(JwtSigningAlgorithm.RS256, JwtSigningAlgorithm.RS384, JwtSigningAlgorithm.RS512)

        `when`("setup hmac based strategy") {
            val result = runCatching {
                hmac.forEach {
                    HmacSha2RefreshTokenStrategy(
                        key = AesKey("803b7e722dc04a8a871eec1a82676f23".toByteArray()),
                        signingAlgorithm = it
                    )
                }
            }

            then("should be no problem") {
                assertThat(result.isSuccess).isTrue()
            }
        }

        `when`("setup non-hmac based strategy") {
            val result = runCatching {
                rsa.forEach {
                    HmacSha2RefreshTokenStrategy(
                        key = AesKey("803b7e722dc04a8a871eec1a82676f23".toByteArray()),
                        signingAlgorithm = it
                    )
                }
            }

            then("should fail") {
                assertThat(result.isFailure).isTrue()
            }
        }
    }
})