package com.aallam.openai.api.batch

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The request counts for different statuses within the batch.
 */
@BetaOpenAI
@Serializable
public data class RequestCounts(
    /** Total number of requests in the batch. */
    @SerialName("total") public val total: Int,

    /** Number of requests that have been completed successfully. */
    @SerialName("completed") public val completed: Int,

    /** Number of requests that have failed. */
    @SerialName("failed") public val failed: Int
)
