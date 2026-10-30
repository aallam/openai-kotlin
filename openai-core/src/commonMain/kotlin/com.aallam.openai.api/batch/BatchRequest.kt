package com.aallam.openai.api.batch

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.core.Endpoint
import com.aallam.openai.api.file.FileId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Creates and executes a batch from an uploaded file of requests
 */
@BetaOpenAI
@Serializable
public data class BatchRequest(
    /**
     * The ID of an uploaded file that contains requests for the new batch.
     *
     * Your input file must be formatted as a JSONL file, and must be uploaded with the purpose `batch`.
     */
    @SerialName("input_file_id") public val inputFileId: FileId,

    /**
     * The endpoint to be used for all requests in the batch. Currently only [Endpoint.Completions] is supported.
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
)

/**
 * Creates a new [BatchRequest].
 */
@BetaOpenAI
public fun batchRequest(block: BatchRequestBuilder.() -> Unit): BatchRequest =
    BatchRequestBuilder().apply(block).build()

/**
 * Builder for [BatchRequest].
 */
@BetaOpenAI
public class BatchRequestBuilder {
    public var inputFileId: FileId? = null
    public var endpoint: Endpoint? = null
    public var completionWindow: CompletionWindow? = null
    public var metadata: Map<String, String>? = null

    public fun build(): BatchRequest = BatchRequest(
        inputFileId = requireNotNull(inputFileId) { "inputFileId is required" },
        endpoint = requireNotNull(endpoint) { "endpoint is required" },
        completionWindow = requireNotNull(completionWindow) { "completionWindow is required" },
        metadata = metadata
    )
}
