package io.imulab.astrea.sdk.oauth

import com.nhaarman.mockitokotlin2.doReturn
import com.nhaarman.mockitokotlin2.mock
import io.imulab.astrea.sdk.`when`
import io.imulab.astrea.sdk.given
import io.imulab.astrea.sdk.oauth.reserved.AuthenticationMethod
import io.imulab.astrea.sdk.then
import org.assertj.core.api.Assertions.assertThat
import org.spekframework.spek2.Spek
import java.time.Duration

object OAuthContextSpec : Spek({

    given("An oauth context") {
        val context = GivenContext(
            mock {
                onGeneric { issuerUrl } doReturn "https://test.com"
                onGeneric { authorizeEndpointUrl } doReturn "https://test.com/oauth/authorize"
                onGeneric { tokenEndpointUrl } doReturn "https://test.com/oauth/token"
                onGeneric { defaultTokenEndpointAuthenticationMethod } doReturn AuthenticationMethod.clientSecretBasic
                onGeneric { authorizeCodeLifespan } doReturn Duration.ofMinutes(10)
                onGeneric { accessTokenLifespan } doReturn Duration.ofMinutes(30)
                onGeneric { refreshTokenLifespan } doReturn Duration.ofDays(14)
                onGeneric { stateEntropy } doReturn 8
            }
        )

        `when`("validates the context") {
            val result = runCatching { context.validate() }

            then("validation should pass") {
                assertThat(result.isSuccess).isTrue()
            }
        }
    }

    given("An oauth context without invalid default token endpoint auth method") {
        val context = GivenContext(
            mock {
                onGeneric { issuerUrl } doReturn "https://test.com"
                onGeneric { authorizeEndpointUrl } doReturn "https://test.com/oauth/authorize"
                onGeneric { tokenEndpointUrl } doReturn "https://test.com/oauth/token"
                onGeneric { defaultTokenEndpointAuthenticationMethod } doReturn "foo"
                onGeneric { authorizeCodeLifespan } doReturn Duration.ofMinutes(10)
                onGeneric { accessTokenLifespan } doReturn Duration.ofMinutes(30)
                onGeneric { refreshTokenLifespan } doReturn Duration.ofDays(14)
                onGeneric { stateEntropy } doReturn 8
            }
        )

        `when`("validates the context") {
            val result = runCatching { context.validate() }

            then("validation should fail") {
                assertThat(result.isFailure).isTrue()
            }
        }
    }
}) {

    /**
     * An implementation to [OAuthContext] which delegates to a mocked instance, since we want to test
     * default methods on the interface.
     */
    class GivenContext(context: OAuthContext) : OAuthContext {
        override val issuerUrl: String = context.issuerUrl
        override val authorizeEndpointUrl: String = context.authorizeEndpointUrl
        override val tokenEndpointUrl: String = context.tokenEndpointUrl
        override val defaultTokenEndpointAuthenticationMethod: String = context.defaultTokenEndpointAuthenticationMethod
        override val authorizeCodeLifespan: Duration = context.authorizeCodeLifespan
        override val accessTokenLifespan: Duration = context.accessTokenLifespan
        override val refreshTokenLifespan: Duration = context.refreshTokenLifespan
        override val stateEntropy: Int = context.stateEntropy
    }
}