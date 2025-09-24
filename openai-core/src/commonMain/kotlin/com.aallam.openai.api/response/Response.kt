package com.aallam.openai.api.response

import com.aallam.openai.api.core.Usage
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response from the responses API.
 */
@Serializable
public data class Response(
    /**
     * Unique identifier for the response.
     */
    @SerialName("id") public val id: String,
    
    /**
     * The object type, always "response".
     */
    @SerialName("object") public val objectType: String,
    
    /**
     * The creation time in epoch seconds.
     */
    @SerialName("created_at") public val createdAt: Double,
    
    /**
     * The model used for the response.
     */
    @SerialName("model") public val model: ModelId,
    
    /**
     * The output items from the response.
     */
    @SerialName("output") public val output: List<ResponseOutputItem>,
    
    /**
     * The reasoning trace from the model (if requested).
     */
    @SerialName("reasoning") public val reasoning: ReasoningTrace? = null,
    
    /**
     * Usage statistics for the response.
     */
    @SerialName("usage") public val usage: Usage? = null,
    
    /**
     * The status of the response.
     */
    @SerialName("status") public val status: String,
    
    /**
     * The combined output text from all message outputs.
     */
    @SerialName("output_text") public val outputText: String? = null,
    
    /**
     * Error information if the response failed.
     */
    @SerialName("error") public val error: ResponseError? = null,
    
    /**
     * Metadata associated with the response.
     */
    @SerialName("metadata") public val metadata: Map<String, String>? = null,
) {
    /**
     * Get the first message output content as text, if available.
     */
    public val firstMessageText: String?
        get() = output.filterIsInstance<Message>()
            .firstOrNull()
            ?.content
            ?.filterIsInstance<MessageContent.OutputText>()
            ?.firstOrNull()
            ?.text
}

/**
 * Input items for the responses API.
 */
@Serializable
public sealed interface ResponseInputItem

/**
 * Output items from the responses API.
 */
@Serializable
public sealed interface ResponseOutputItem

/**
 * Error information for a failed response.
 */
@Serializable
public data class ResponseError(
    /**
     * The error code.
     */
    @SerialName("code") public val code: String? = null,
    
    /**
     * The error message.
     */
    @SerialName("message") public val message: String? = null,
)
