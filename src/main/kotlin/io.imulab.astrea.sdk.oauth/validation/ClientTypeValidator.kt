package io.imulab.astrea.sdk.oauth.validation

import io.imulab.astrea.sdk.oauth.error.ServerError
import io.imulab.astrea.sdk.oauth.reserved.ClientType

/**
 * Validates `client_type = {public, confidential}`.
 */
object ClientTypeValidator : SpecDefinitionValidator {
    override fun validate(value: String): String {
        return when (value) {
            ClientType.public, ClientType.confidential -> value
            else -> throw ServerError.internal("Illegal client type <$value>.")
        }
    }
}