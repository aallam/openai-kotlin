package com.aallam.openai.api.batch

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.core.Endpoint
import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.exception.OpenAIErrorDetails
import com.aallam.openai.api.file.FileId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a batch object.
 */
@BetaOpenAI
@Serializable
public data class Batch(
    /** Unique identifier for the batch. */
    @SerialName("id") public val id: BatchId,

    /** The OpenAI API endpoint used by the batch. */
    @SerialName("endpoint") public val endpoint: Endpoint,

    /** Container for any errors occurred during batch processing. */
    @SerialName("errors") public val errors: PaginatedList<OpenAIErrorDetails>?,

    /** Identifier of the input file for the batch. */
    @SerialName("input_file_id") public val inputFileId: FileId? = null,

    /** Time frame within which the batch should be processed. */
    @SerialName("completion_window") public val completionWindow: CompletionWindow? = null,

    /** Current processing status of the batch. */
    @SerialName("status") public val status: Status? = null,

    /** Identifier of the output file containing successfully executed requests. */
    @SerialName("output_file_id") public val outputFileId: FileId? = null,

    /** Identifier of the error file containing outputs of requests with errors. */
    @SerialName("error_file_id") public val errorFileId: FileId? = null,

    /** Unix timestamp for when the batch was created. */
    @SerialName("created_at") public val createdAt: Long? = null,

    /** Unix timestamp for when the batch processing started. */
    @SerialName("in_progress_at") public val inProgressAt: Long? = null,

    /** Unix timestamp for when the batch will expire. */
    @SerialName("expires_at") public val expiresAt: Long? = null,

    /** Unix timestamp for when the batch started finalizing. */
    @SerialName("finalizing_at") public val finalizingAt: Long? = null,

    /** Unix timestamp for when the batch was completed. */
    @SerialName("completed_at") public val completedAt: Long? = null,

    /** Unix timestamp for when the batch failed. */
    @SerialName("failed_at") public val failedAt: Long? = null,

    /** Unix timestamp for when the batch expired. */
    @SerialName("expired_at") public val expiredAt: Long? = null,

    /** Unix timestamp for when the batch started cancelling. */
    @SerialName("cancelling_at") public val cancellingAt: Long? = null,

    /** Unix timestamp for when the batch was cancelled. */
    @SerialName("cancelled_at") public val cancelledAt: Long? = null,

    /** Container for the counts of requests by their status. */
    @SerialName("request_counts") public val requestCounts: RequestCounts? = null,

    /** Metadata associated with the batch as key-value pairs. */
    @SerialName("metadata") public val metadata: Map<String, String>? = null
)
