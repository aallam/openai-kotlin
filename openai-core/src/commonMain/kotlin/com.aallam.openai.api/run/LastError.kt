package com.aallam.openai.api.run

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The last error associated with this run/step.
 */
@BetaOpenAI
@Serializable
public data class LastError(
    /**
     * One of server_error or rate_limit_exceeded.
     */
    @SerialName("code") public val code: String,

    /**
     * A human-readable description of the error.
     */
    @SerialName("message") public val message: String,
)
