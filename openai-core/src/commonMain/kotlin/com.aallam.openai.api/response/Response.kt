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
@Serializable(with = ResponseSerializer::class)
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
    /** Whether processing runs in the background. */
    @SerialName("background") public val background: Boolean? = null,
    /** Conversation associated with this response. */
    @SerialName("conversation") public val conversation: JsonObject? = null,
    /** Actual processing tier. */
    @SerialName("service_tier") public val serviceTier: String? = null,
    /** Completion timestamp. */
    @SerialName("completed_at") public val completedAt: Long? = null,
    /** Hosted tool call limit. */
    @SerialName("max_tool_calls") public val maxToolCalls: Int? = null,
    /** Cache routing key. */
    @SerialName("prompt_cache_key") public val promptCacheKey: String? = null,
    /** Effective cache options. */
    @SerialName("prompt_cache_options") public val promptCacheOptions: ResponsePromptCacheOptions? = null,
    /** Context management settings. */
    @SerialName("context_management") public val contextManagement: List<ResponseContextManagement>? = null,
    /** Effective restricted access program configuration. */
    @SerialName("access_programs") public val accessPrograms: ResponseAccessPrograms? = null,
    /** End-user safety identifier. */
    @SerialName("safety_identifier") public val safetyIdentifier: String? = null,
    /** Requested token log probability count. */
    @SerialName("top_logprobs") public val topLogprobs: Int? = null,
    /** Input/output moderation results. */
    @SerialName("moderation") public val moderation: JsonElement? = null,
    /**
     * Original decoded payload, including unknown fields. This snapshot is unchanged by [copy].
     * Encoding a [Response] writes its typed properties only; encode this snapshot directly to
     * export every original field or reuse the original output as continuation input.
     */
    public val rawData: JsonObject? = null,

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
    /** Encrypted reasoning or compaction context. */
    @SerialName("encrypted_content") public val encryptedContent: String? = null,
    /** Generated image result. */
    @SerialName("result") public val result: String? = null,
    /** MCP server label. */
    @SerialName("server_label") public val serverLabel: String? = null,
    /** Tool output, including strings and structured content. */
    @SerialName("output") public val output: JsonElement? = null,
    /** MCP approval request identifier. */
    @SerialName("approval_request_id") public val approvalRequestId: String? = null,
    /** Hosted tool action details. */
    @SerialName("action") public val action: JsonElement? = null,
    /** Tool error details. */
    @SerialName("error") public val error: JsonElement? = null,
    /** MCP server tool declarations. */
    @SerialName("tools") public val tools: JsonElement? = null,
    /** File search queries. */
    @SerialName("queries") public val queries: List<String>? = null,
    /** Included file search results. */
    @SerialName("results") public val results: JsonElement? = null,
    /** Container used by the tool. */
    @SerialName("container_id") public val containerId: String? = null,
    /** Executed code. */
    @SerialName("code") public val code: String? = null,
    /** Code execution outputs. */
    @SerialName("outputs") public val outputs: JsonElement? = null,

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
    /** Output token log probabilities. */
    @SerialName("logprobs") public val logprobs: JsonElement? = null,

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
