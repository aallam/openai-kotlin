package com.aallam.openai.api.response

import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/** Input for compacting accumulated conversation context. */
@Serializable
public data class ResponseCompactRequest(
    /** Model used to compact the context. */
    @SerialName("model") public val model: ModelId,
    /** Context to compact. */
    @SerialName("input") public val input: ResponseInput? = null,
    /** System instructions applied while compacting. */
    @SerialName("instructions") public val instructions: String? = null,
    /** Stored response providing preceding context. */
    @SerialName("previous_response_id") public val previousResponseId: ResponseId? = null,
    /** Cache routing key. */
    @SerialName("prompt_cache_key") public val promptCacheKey: String? = null,
    /** Cache configuration. */
    @SerialName("prompt_cache_options") public val promptCacheOptions: ResponsePromptCacheOptions? = null,
    /** Requested processing tier. */
    @SerialName("service_tier") public val serviceTier: String? = null,
)

/** Compacted context items that can be supplied on subsequent turns. */
@Serializable
public data class ResponseCompaction(
    /** Compaction identifier. */
    @SerialName("id") public val id: String,
    /** Unmodified compacted context, including tool fields and encrypted compaction items. */
    @SerialName("output") public val output: List<JsonObject>,
    /** Object discriminator. */
    @SerialName("object") public val objectType: String? = null,
    /** Creation timestamp. */
    @SerialName("created_at") public val createdAt: Long? = null,
    /** Compaction token usage. */
    @SerialName("usage") public val usage: ResponseUsage? = null,
)

/** Input whose token count is estimated without generating a response. */
@Serializable
public data class ResponseTokenCountRequest(
    /** Model whose tokenizer is used. */
    @SerialName("model") public val model: ModelId? = null,
    /** Text or structured context. */
    @SerialName("input") public val input: ResponseInput? = null,
    /** Instructions to include. */
    @SerialName("instructions") public val instructions: String? = null,
    /** Conversation supplying context. */
    @SerialName("conversation") public val conversation: String? = null,
    /** Prior stored response supplying context. */
    @SerialName("previous_response_id") public val previousResponseId: ResponseId? = null,
    /** Whether tools can execute in parallel. */
    @SerialName("parallel_tool_calls") public val parallelToolCalls: Boolean? = null,
    /** Reasoning configuration included in the estimate. */
    @SerialName("reasoning") public val reasoning: ResponseReasoningOptions? = null,
    /** Output format included in the estimate. */
    @SerialName("text") public val text: ResponseTextOptions? = null,
    /** Tool selection configuration. */
    @SerialName("tool_choice") public val toolChoice: JsonElement? = null,
    /** Available tools included in the estimate. */
    @SerialName("tools") public val tools: List<ResponseToolDefinition>? = null,
    /** Context truncation strategy. */
    @SerialName("truncation") public val truncation: String? = null,
    /** Personality configuration included in the estimate. */
    @SerialName("personality") public val personality: String? = null,
)

/** Token estimate for a prospective response. */
@Serializable
public data class ResponseTokenCount(
    /** Number of input tokens. */
    @SerialName("input_tokens") public val inputTokens: Int,
    /** Object discriminator. */
    @SerialName("object") public val objectType: String? = null,
)
