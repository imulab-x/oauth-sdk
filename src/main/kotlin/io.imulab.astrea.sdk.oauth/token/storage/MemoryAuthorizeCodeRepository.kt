package io.imulab.astrea.sdk.oauth.token.storage

import io.imulab.astrea.sdk.oauth.error.InvalidGrant
import io.imulab.astrea.sdk.oauth.request.OAuthRequest

class MemoryAuthorizeCodeRepository : AuthorizeCodeRepository {

    private val db = mutableMapOf<String, OAuthRequest>()

    override suspend fun createAuthorizeCodeSession(code: String, request: OAuthRequest) {
        db[code] = request
    }

    override suspend fun getAuthorizeCodeSession(code: String): OAuthRequest {
        return db[code] ?: throw InvalidGrant.invalid()
    }

    override suspend fun invalidateAuthorizeCodeSession(code: String) {
        db.remove(code)
    }
}