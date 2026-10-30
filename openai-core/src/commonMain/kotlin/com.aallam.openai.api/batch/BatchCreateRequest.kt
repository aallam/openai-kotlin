package com.aallam.openai.api.batch

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.core.Endpoint
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileExpiration
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Creates and executes a batch from an uploaded file of requests
 */
@BetaOpenAI
@Serializable
public data class BatchCreateRequest(
    /**
     * The ID of an uploaded file that contains requests for the new batch.
     *
     * Your input file must be formatted as a JSONL file, and must be uploaded with the purpose `batch`.
     */
    @SerialName("input_file_id") public val inputFileId: FileId,

    /**
     * The endpoint to be used for all requests in the batch. Use an endpoint supported by the selected batch model.
     */
    @SerialName("endpoint") public val endpoint: Endpoint,

    /**
     * The time frame within which the batch should be processed. Currently only `24h` is supported.
     */
    @SerialName("completion_window") public val completionWindow: CompletionWindow,

    /**
     * Optional custom metadata for the batch.
     */
    @SerialName("metadata") public val metadata: Map<String, String>? = null,

    /** Expiration policy for output and error files. */
    @SerialName("output_expires_after") public val outputExpiresAfter: FileExpiration? = null,
)

/**
 * Creates a new [BatchCreateRequest].
 */
@BetaOpenAI
public fun batchCreateRequest(block: BatchCreateRequestBuilder.() -> Unit): BatchCreateRequest =
    BatchCreateRequestBuilder().apply(block).build()

/**
 * Builder for [BatchCreateRequest].
 */
@BetaOpenAI
public class BatchCreateRequestBuilder {
    public var inputFileId: FileId? = null
    public var endpoint: Endpoint? = null
    public var completionWindow: CompletionWindow? = null
    public var metadata: Map<String, String>? = null

    /** Expiration policy for output and error files. */
    public var outputExpiresAfter: FileExpiration? = null

    public fun build(): BatchCreateRequest = BatchCreateRequest(
        inputFileId = requireNotNull(inputFileId) { "inputFileId is required" },
        endpoint = requireNotNull(endpoint) { "endpoint is required" },
        completionWindow = requireNotNull(completionWindow) { "completionWindow is required" },
        metadata = metadata,
        outputExpiresAfter = outputExpiresAfter,
    )
}
