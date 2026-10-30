package com.aallam.openai.api.response

import com.aallam.openai.api.chat.ChatAnnotation
import com.aallam.openai.api.exception.OpenAIErrorDetails
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/**
 * A model response object.
 */
@Serializable
public data class Response(
    @SerialName("id") public val id: ResponseId,
    @SerialName("object") public val objectType: String? = null,
    @SerialName("created_at") public val createdAt: Long? = null,
    @SerialName("status") public val status: String? = null,
    @SerialName("error") public val error: OpenAIErrorDetails? = null,
    @SerialName("incomplete_details") public val incompleteDetails: JsonObject? = null,
    @SerialName("instructions") public val instructions: String? = null,
    @SerialName("max_output_tokens") public val maxOutputTokens: Int? = null,
    @SerialName("model") public val model: ModelId? = null,
    @SerialName("output") public val output: List<ResponseOutputItem> = emptyList(),
    @SerialName("parallel_tool_calls") public val parallelToolCalls: Boolean? = null,
    @SerialName("previous_response_id") public val previousResponseId: ResponseId? = null,
    @SerialName("reasoning") public val reasoning: ResponseReasoning? = null,
    @SerialName("store") public val store: Boolean? = null,
    @SerialName("temperature") public val temperature: Double? = null,
    @SerialName("text") public val text: JsonObject? = null,
    @SerialName("tool_choice") public val toolChoice: JsonElement? = null,
    @SerialName("tools") public val tools: JsonElement? = null,
    @SerialName("top_p") public val topP: Double? = null,
    @SerialName("truncation") public val truncation: String? = null,
    @SerialName("usage") public val usage: ResponseUsage? = null,
    @SerialName("user") public val user: String? = null,
    @SerialName("metadata") public val metadata: Map<String, String>? = null,
    @SerialName("output_text") public val outputText: String? = null,
)

/**
 * Output item from a response.
 */
@Serializable
public data class ResponseOutputItem(
    @SerialName("id") public val id: String? = null,
    @SerialName("type") public val type: String? = null,
    @SerialName("status") public val status: String? = null,
    @SerialName("role") public val role: String? = null,
    @SerialName("content") public val content: List<ResponseOutputContent>? = null,
    @SerialName("name") public val name: String? = null,
    @SerialName("arguments") public val arguments: String? = null,
    @SerialName("call_id") public val callId: String? = null,
    @SerialName("summary") public val summary: JsonElement? = null,
)

/**
 * Content item inside a response output message.
 */
@Serializable
public data class ResponseOutputContent(
    @SerialName("type") public val type: String? = null,
    @SerialName("text") public val text: String? = null,
    @SerialName("annotations") public val annotations: List<ChatAnnotation>? = null,
    @SerialName("refusal") public val refusal: String? = null,
    @SerialName("reasoning") public val reasoning: JsonElement? = null,
    @SerialName("reasoning_content") public val reasoningContent: String? = null,
)

/**
 * Token usage for a response.
 */
@Serializable
public data class ResponseUsage(
    @SerialName("input_tokens") public val inputTokens: Int? = null,
    @SerialName("input_tokens_details") public val inputTokensDetails: ResponseInputTokensDetails? = null,
    @SerialName("output_tokens") public val outputTokens: Int? = null,
    @SerialName("output_tokens_details") public val outputTokensDetails: ResponseOutputTokensDetails? = null,
    @SerialName("total_tokens") public val totalTokens: Int? = null,
)

/**
 * Input token details.
 */
@Serializable
public data class ResponseInputTokensDetails(
    @SerialName("cached_tokens") public val cachedTokens: Int? = null,
)

/**
 * Output token details.
 */
@Serializable
public data class ResponseOutputTokensDetails(
    @SerialName("reasoning_tokens") public val reasoningTokens: Int? = null,
)
