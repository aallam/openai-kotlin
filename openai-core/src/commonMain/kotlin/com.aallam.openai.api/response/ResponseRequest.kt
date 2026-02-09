package com.aallam.openai.api.response

import com.aallam.openai.api.chat.ChatResponseFormat
import com.aallam.openai.api.chat.Effort
import com.aallam.openai.api.chat.SearchContextSize
import com.aallam.openai.api.chat.UserLocation
import com.aallam.openai.api.core.Parameters
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement

/**
 * Creates a model response.
 */
@Serializable
public data class ResponseRequest(
    /**
     * Model to use for this response.
     */
    @SerialName("model") public val model: ModelId,

    /**
     * Text or structured input.
     */
    @SerialName("input") public val input: ResponseInput? = null,

    /**
     * Optional instructions that prepended to model context.
     */
    @SerialName("instructions") public val instructions: String? = null,

    /**
     * Upper bound for generated output tokens.
     */
    @SerialName("max_output_tokens") public val maxOutputTokens: Int? = null,

    /**
     * Arbitrary metadata for this response.
     */
    @SerialName("metadata") public val metadata: Map<String, String>? = null,

    /**
     * Whether the model can execute multiple tool calls in parallel.
     */
    @SerialName("parallel_tool_calls") public val parallelToolCalls: Boolean? = null,

    /**
     * Continue from a previous response.
     */
    @SerialName("previous_response_id") public val previousResponseId: ResponseId? = null,

    /**
     * Reasoning configuration.
     */
    @SerialName("reasoning") public val reasoning: ResponseReasoning? = null,

    /**
     * Whether to store this response for evals/distillation products.
     */
    @SerialName("store") public val store: Boolean? = null,

    /**
     * Sampling temperature.
     */
    @SerialName("temperature") public val temperature: Double? = null,

    /**
     * Text output format configuration.
     */
    @SerialName("text") public val text: ResponseText? = null,

    /**
     * Tool choice strategy.
     */
    @SerialName("tool_choice") public val toolChoice: JsonElement? = null,

    /**
     * Tools available to the model.
     */
    @SerialName("tools") public val tools: List<ResponseTool>? = null,

    /**
     * Nucleus sampling parameter.
     */
    @SerialName("top_p") public val topP: Double? = null,

    /**
     * Truncation strategy.
     */
    @SerialName("truncation") public val truncation: String? = null,

    /**
     * End-user identifier.
     */
    @SerialName("user") public val user: String? = null,
)

/**
 * Reasoning configuration for response requests.
 */
@Serializable
public data class ResponseReasoning(
    @SerialName("effort") public val effort: Effort? = null,
)

/**
 * Text output configuration for response requests.
 */
@Serializable
public data class ResponseText(
    @SerialName("format") public val format: ChatResponseFormat? = null,
)

/**
 * Tool declaration for response requests.
 */
@Serializable
public data class ResponseTool(
    @SerialName("type") public val type: String,
    @SerialName("name") public val name: String? = null,
    @SerialName("description") public val description: String? = null,
    @SerialName("parameters") public val parameters: Parameters? = null,
    @SerialName("strict") public val strict: Boolean? = null,
    @SerialName("search_context_size") public val searchContextSize: SearchContextSize? = null,
    @SerialName("user_location") public val userLocation: UserLocation? = null,
)
