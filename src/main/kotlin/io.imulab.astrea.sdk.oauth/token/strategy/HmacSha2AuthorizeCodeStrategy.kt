package io.imulab.astrea.sdk.oauth.token.strategy

import io.imulab.astrea.sdk.oauth.error.InvalidGrant
import io.imulab.astrea.sdk.oauth.request.OAuthAuthorizeRequest
import io.imulab.astrea.sdk.oauth.reserved.dot
import io.imulab.astrea.sdk.oauth.token.JwtSigningAlgorithm
import org.jose4j.jca.ProviderContext
import org.jose4j.jws.HmacUsingShaAlgorithm
import java.security.Key
import java.util.*
import java.util.concurrent.ThreadLocalRandom

class HmacSha2AuthorizeCodeStrategy(
    private val key: Key,
    signingAlgorithm: JwtSigningAlgorithm,
    private val codeLength: Int = 16
) : AuthorizeCodeStrategy {

    private val encoder = Base64.getUrlEncoder().withoutPadding()
    private val decoder = Base64.getUrlDecoder()
    private val hmac = when (signingAlgorithm) {
        JwtSigningAlgorithm.HS256 -> HmacUsingShaAlgorithm.HmacSha256()
        JwtSigningAlgorithm.HS384 -> HmacUsingShaAlgorithm.HmacSha384()
        JwtSigningAlgorithm.HS512 -> HmacUsingShaAlgorithm.HmacSha512()
        else -> throw IllegalArgumentException("not an hmac-sha2 algorithm.")
    }

    override fun computeIdentifier(code: String): String {
        val parts = code.split(dot)
        if (parts.size != 2)
            throw InvalidGrant.invalid()
        return parts[1]
    }

    override suspend fun generateCode(request: OAuthAuthorizeRequest): String {
        val randomBytes = ByteArray(codeLength).also { ThreadLocalRandom.current().nextBytes(it) }
        val signatureBytes = hmac.sign(key, randomBytes, ProviderContext())
        return encoder.encodeToString(randomBytes) + dot + encoder.encodeToString(signatureBytes)
    }

    override suspend fun verifyCode(code: String, request: OAuthAuthorizeRequest) {
        val parts = code.split(dot)
        if (parts.size != 2)
            throw InvalidGrant.invalid()

        if (!hmac.verifySignature(
                decoder.decode(parts[1]),
                key,
                decoder.decode(parts[0]),
                ProviderContext()
            )
        ) {
            throw InvalidGrant.invalid()
        }
    }
}