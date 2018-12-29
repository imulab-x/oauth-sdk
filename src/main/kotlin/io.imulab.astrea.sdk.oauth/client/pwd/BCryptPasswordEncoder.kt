package io.imulab.astrea.sdk.oauth.client.pwd

import org.mindrot.jbcrypt.BCrypt

/**
 * Implementation of [PasswordEncoder] that uses bcrypt algorithm for encoding.
 */
class BCryptPasswordEncoder(private val complexity: Int = 10) : PasswordEncoder {

    override fun encode(plain: String): String {
        return BCrypt.hashpw(plain, BCrypt.gensalt(complexity))
    }

    override fun matches(raw: String, encoded: String): Boolean {
        return try {
            BCrypt.checkpw(raw, encoded)
        } catch (e: Exception) {
            false
        }
    }
}