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
public data class ResponseCreateRequest(
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
    @SerialName("reasoning") public val reasoning: ResponseReasoningOptions? = null,

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
    @SerialName("text") public val text: ResponseTextOptions? = null,

    /**
     * Tool choice strategy.
     */
    @SerialName("tool_choice") public val toolChoice: JsonElement? = null,

    /**
     * Tools available to the model.
     */
    @SerialName("tools") public val tools: List<ResponseToolDefinition>? = null,

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
    /** Run asynchronously in the background. */
    @SerialName("background") public val background: Boolean? = null,
    /** Context compaction configuration. */
    @SerialName("context_management") public val contextManagement: List<ResponseContextManagement>? = null,
    /** Conversation whose items are prepended and updated. */
    @SerialName("conversation") public val conversation: String? = null,
    /** Additional response fields to include. */
    @SerialName("include") public val include: List<String>? = null,
    /** Maximum hosted-tool calls. */
    @SerialName("max_tool_calls") public val maxToolCalls: Int? = null,
    /** Moderation policy. */
    @SerialName("moderation") public val moderation: ResponseModeration? = null,
    /** Stable cache routing key. */
    @SerialName("prompt_cache_key") public val promptCacheKey: String? = null,
    /** Prompt caching controls. */
    @SerialName("prompt_cache_options") public val promptCacheOptions: ResponsePromptCacheOptions? = null,
    /** Stable end-user safety identifier. */
    @SerialName("safety_identifier") public val safetyIdentifier: String? = null,
    /** Requested processing tier. */
    @SerialName("service_tier") public val serviceTier: String? = null,
    /** Streaming payload options. */
    @SerialName("stream_options") public val streamOptions: ResponseStreamOptions? = null,
    /** Number of output token log probabilities. */
    @SerialName("top_logprobs") public val topLogprobs: Int? = null,
    /** Restricted program configuration. */
    @SerialName("access_programs") public val accessPrograms: ResponseAccessPrograms? = null,

)

/**
 * Reasoning configuration for response requests.
 */
@Serializable
public data class ResponseReasoningOptions(
    @SerialName("effort") public val effort: Effort? = null,
    /** Reasoning summary detail. */
    @SerialName("summary") public val summary: String? = null,
    /** Reasoning history included on subsequent turns. */
    @SerialName("context") public val context: String? = null,
    /** Reasoning execution mode. */
    @SerialName("mode") public val mode: String? = null,
)

/**
 * Text output configuration for response requests.
 */
@Serializable
public data class ResponseTextOptions(
    @Serializable(with = ResponseFormatSerializer::class)
    @SerialName("format") public val format: ChatResponseFormat? = null,
    /** Requested text verbosity. */
    @SerialName("verbosity") public val verbosity: String? = null,
)

/**
 * Tool declaration for response requests.
 */
@Serializable
public data class ResponseToolDefinition(
    @SerialName("type") public val type: String,
    @SerialName("name") public val name: String? = null,
    @SerialName("description") public val description: String? = null,
    @SerialName("parameters") public val parameters: Parameters? = null,
    @SerialName("strict") public val strict: Boolean? = null,
    @SerialName("search_context_size") public val searchContextSize: SearchContextSize? = null,
    @SerialName("user_location") public val userLocation: UserLocation? = null,
    /** Vector stores searched by file search. */
    @SerialName("vector_store_ids") public val vectorStoreIds: List<String>? = null,
    /** Search filter expression. */
    @SerialName("filters") public val filters: JsonElement? = null,
    /** File search ranking controls. */
    @SerialName("ranking_options") public val rankingOptions: ResponseRankingOptions? = null,
    /** Maximum search results. */
    @SerialName("max_num_results") public val maxNumResults: Int? = null,
    /** MCP server label. */
    @SerialName("server_label") public val serverLabel: String? = null,
    /** Remote MCP server URL. */
    @SerialName("server_url") public val serverUrl: String? = null,
    /** MCP connector identifier. */
    @SerialName("connector_id") public val connectorId: String? = null,
    /** MCP server description. */
    @SerialName("server_description") public val serverDescription: String? = null,
    /** MCP server request headers; may contain credentials. */
    @SerialName("headers") public val headers: Map<String, String>? = null,
    /** MCP authorization token. */
    @SerialName("authorization") public val authorization: String? = null,
    /** MCP tool names or filter. */
    @SerialName("allowed_tools") public val allowedTools: JsonElement? = null,
    /** MCP approval policy. */
    @SerialName("require_approval") public val requireApproval: JsonElement? = null,
    /** Code interpreter container ID or configuration. */
    @SerialName("container") public val container: JsonElement? = null,
    /** Custom tool input format. */
    @SerialName("format") public val format: JsonElement? = null,
    /** Image background mode. */
    @SerialName("background") public val background: String? = null,
    /** Image quality. */
    @SerialName("quality") public val quality: String? = null,
    /** Image dimensions. */
    @SerialName("size") public val size: String? = null,
    /** Generated image format. */
    @SerialName("output_format") public val outputFormat: String? = null,
    /** Generated image compression. */
    @SerialName("output_compression") public val outputCompression: Int? = null,
    /** Number of streamed partial images. */
    @SerialName("partial_images") public val partialImages: Int? = null,
    /** Image moderation level. */
    @SerialName("moderation") public val moderation: String? = null,
    /** Image generation model. */
    @SerialName("model") public val model: String? = null,
    /** Callers allowed to invoke this tool. */
    @SerialName("allowed_callers") public val allowedCallers: List<String>? = null,
    /** Whether tool execution can be asynchronous. */
    @SerialName("async") public val async: Boolean? = null,
    /** Defer loading until tool search selects the tool. */
    @SerialName("defer_loading") public val deferLoading: Boolean? = null,
    /** Function output JSON schema. */
    @SerialName("output_schema") public val outputSchema: JsonElement? = null,
    /** Computer display height. */
    @SerialName("display_height") public val displayHeight: Int? = null,
    /** Computer display width. */
    @SerialName("display_width") public val displayWidth: Int? = null,
    /** Computer environment name or shell environment configuration. */
    @SerialName("environment") public val environment: JsonElement? = null,
    /** Whether web search can access external sources. */
    @SerialName("external_web_access") public val externalWebAccess: Boolean? = null,
    /** Secure MCP tunnel identifier. */
    @SerialName("tunnel_id") public val tunnelId: String? = null,
    /** Image generation action. */
    @SerialName("action") public val action: String? = null,
    /** Image edit fidelity. */
    @SerialName("input_fidelity") public val inputFidelity: String? = null,
    /** Mask for image editing. */
    @SerialName("input_image_mask") public val inputImageMask: ResponseImageMask? = null,
    /** Functions or custom tools in a namespace. */
    @SerialName("tools") public val tools: List<ResponseToolDefinition>? = null,
    /** Tool search execution location. */
    @SerialName("execution") public val execution: String? = null,
    /** Web search result content types. */
    @SerialName("search_content_types") public val searchContentTypes: List<String>? = null,
)
