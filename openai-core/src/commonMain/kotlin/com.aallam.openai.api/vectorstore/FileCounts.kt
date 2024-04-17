package com.aallam.openai.api.vectorstore

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The number of files that are currently being processed.
 */
@Serializable
public data class FileCounts(
    /**
     * The number of files that are currently being processed.
     */
    @SerialName("in_progress") public val inProgress: Long,

    /**
     * The number of files that have been successfully processed.
     */
    @SerialName("completed") public val completed: Long,

    /**
     * The number of files that have failed to process.
     */
    @SerialName("failed") public val failed: Long,

    /**
     * The number of files that were canceled.
     */
    @SerialName("cancelled") public val cancelled: Long,

    /**
     * The total number of files.
     */
    @SerialName("total") public val total: Long,
)