package io.imulab.astrea.sdk.oauth.client.authn

import io.imulab.astrea.sdk.oauth.client.OAuthClient
import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.sdk.oauth.request.OAuthRequestForm
import io.imulab.astrea.sdk.oauth.reserved.AuthenticationMethod

/**
 * Main point of entry client authenticator that delegate work to a list of [authenticators]
 * depending on the supported method. If no authenticator was able to take on work, it
 * raises server_error.
 */
open class ClientAuthenticators(
    private val authenticators: List<ClientAuthenticator>,
    private val defaultMethod: String = AuthenticationMethod.clientSecretPost,
    private val methodFinder: suspend (OAuthRequestForm) -> String = { defaultMethod }
) {

    suspend fun authenticate(form: OAuthRequestForm): OAuthClient {
        val method = methodFinder(form)
        return authenticators.find { it.supports(method) }?.authenticate(form)
            ?: throw ServerError.internal("No client authenticators configured for method <$method>.")
    }
}