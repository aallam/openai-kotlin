package com.aallam.openai.api.responses

import com.aallam.openai.api.OpenAIDsl
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Request object for the OpenAI Responses API */
@Serializable
public data class ResponseRequest(

    /** Text, image, or file inputs to the model, used to generate a response. */
    @SerialName("input") val input: ResponseInput,

    /**
     * Model ID used to generate the response, like gpt-4o or o1.
     * OpenAI offers a wide range of models with different capabilities, performance characteristics, and price points.
     * Refer to the model guide to browse and compare available models.
     */
    @SerialName("model") val model: ModelId,

    /**
     * Whether to run the model response in the background.
     * Defaults to false.
     */
    @SerialName("background")
    val background: Boolean? = null,

    /** Specify additional output data to include in the model response. */
    @SerialName("include") val include: List<ResponseIncludable>? = null,

    /**
     * Inserts a system (or developer) message as the first item in the model's context.
     *
     * When using along with previous_response_id, the instructions from a previous response will not be carried over to the next response.
     * This makes it simple to swap out system (or developer) messages in new responses.
     */
    @SerialName("instructions") val instructions: String? = null,

    /** An upper bound for the number of tokens that can be generated for a response, including visible output tokens and reasoning tokens. */
    @SerialName("max_output_tokens") val maxOutputTokens: Int? = null,

    /**
     * The maximum number of total calls to built-in tools that can be processed in a response.
     * This maximum number applies across all built-in tool calls, not per individual tool.
     * Any further attempts to call a tool by the model will be ignored.
     */
    @SerialName("max_tool_calls") val maxToolCalls: Int? = null,

    /**
     * Set of key-value pairs that can be attached to an object. This can be
     * useful for storing additional information about the object in a structured
     * format, and querying for objects via API or the dashboard.
     *
     * Keys are strings with a maximum length of 64 characters. Values are strings
     * with a maximum length of 512 characters.
     * */
    @SerialName("metadata") val metadata: Map<String, String>? = null,

    /** Whether to allow the model to run tool calls in parallel. */
    @SerialName("parallel_tool_calls") val parallelToolCalls: Boolean? = null,

    /** The unique ID of the previous response to the model. Use this to create multi-turn conversations. */
    @SerialName("previous_response_id") val previousResponseId: String? = null,

    /** Reference to a prompt template and its variables. */
    @SerialName("prompt")
    val prompt: PromptTemplate? = null,

    /** Used by OpenAI to cache responses for similar requests to optimize your cache hit rates. Replaces the `user` field. */
    @SerialName("prompt_cache_key") val promptCacheKey: String? = null,

    /** Configuration for reasoning models. */
    @SerialName("reasoning") val reasoning: ReasoningConfig? = null,

    /**
     * A stable identifier used to help detect users of your application that may be violating OpenAI's usage policies.
     * The IDs should be a string that uniquely identifies each user.
     * We recommend hashing their username or email address, in order to avoid sending us any identifying information.
     */
    @SerialName("safety_identifier") val safetyIdentifier: String? = null,

    /**
     * Specifies the processing type used for serving the request.
     *
     * - If set to `auto`, then the request will be processed with the service tier configured in the Project settings.
     *   Unless otherwise configured, the Project will use 'default'.
     * - If set to `default`, then the request will be processed with the standard pricing and performance for the selected model.
     * - If set to `flex` or `priority`, then the request will be processed with the corresponding service tier.
     * - When not set, the default behavior is `auto`.
     *
     * When the service_tier parameter is set, the response body will include the service_tier value based on the processing mode actually used to serve the request.
     * This response value may be different from the value set in the parameter.
     */
    @SerialName("service_tier")
    val serviceTier: ServiceTier? = null,

    /** Whether to store the generated model response for later retrieval via API. */
    @SerialName("store") val store: Boolean? = null,

    /**
     * If set to true, the model response data will be streamed to the client as it is generated using server-sent events. See the Streaming section below for more information.
     */
    @SerialName("stream") val stream: Boolean? = null,

    /**
     * What sampling temperature to use, between 0 and 2.
     * Higher values like 0.8 will make the output more random,
     * while lower values like 0.2 will make it more focused and deterministic.
     * We generally recommend altering this or top_p but not both.
     */
    @SerialName("temperature") val temperature: Double? = null,

    /** Configuration options for a text response from the model. Can be plain text or structured JSON data. */
    @SerialName("text") val text: ResponseTextConfig? = null,

    /** How the model should select which tool (or tools) to use when generating a response. See the tools parameter to see how to specify which tools the model can call. */
    @SerialName("tool_choice") val toolChoice: ResponseToolChoiceConfig? = null,

    /**
     * An array of tools the model may call while generating a response. You can specify which tool to use by setting the `tool_choice` parameter.
     *
     * The two categories of tools you can provide the model are:
     *
     * - Built-in tools: Tools that are provided by OpenAI that extend the model's capabilities, like web search or file search. Learn more about built-in tools.
     * - Function calls (custom tools): Functions that are defined by you, enabling the model to call your own code. Learn more about function calling.
     */
    @SerialName("tools") val tools: List<ResponseTool>? = null,

    /** An integer between 0 and 20 specifying the number of most likely tokens to return at each token position, each with an associated log probability. */
    @SerialName("top_logprobs") val topLogprobs: Int? = null,

    /**
     * An alternative to sampling with temperature, called nucleus sampling, where the model considers the results of the tokens with top_p probability mass. So 0.1 means only the tokens comprising the top 10% probability mass are considered.
     *
     * We generally recommend altering this or temperature but not both.
     */
    @SerialName("top_p") val topP: Double? = null,

    /**
     * The truncation strategy to use for the model response.
     * - `auto`: If the context exceeds the model's context window size, the model will truncate
     *    the response by dropping input items in the middle of the conversation.
     * - `disabled` (default): If a model response will exceed the context window size,
     *    the request will fail with a 400 error.
     */
    @SerialName("truncation") val truncation: Truncation? = null,

    /**
     * A stable identifier for your end-users.
     * Used to boost cache hit rates by better bucketing similar requests and to help OpenAI detect and prevent abuse.
     */
    @Deprecated("This field is being replaced by safety_identifier and prompt_cache_key. Use prompt_cache_key instead to maintain caching optimizations.")
    @SerialName("user") val user: String? = null
)

/** Builder for ResponseRequest objects */
@OpenAIDsl
public class ResponseRequestBuilder {
    /** Whether to run the model response in the background */
    public var background: Boolean? = null

    /** ID of the model to use */
    public var model: ModelId? = null

    /** The input to the model */
    public var input: ResponseInput? = null

    /** Specify additional output data to include in the model response */
    public var include: List<ResponseIncludable>? = null

    /** Instructions for the model */
    public var instructions: String? = null

    /** Maximum number of tokens to generate */
    public var maxOutputTokens: Int? = null

    /** The maximum number of total calls to built-in tools that can be processed in a response */
    public var maxToolCalls: Int? = null

    /** Custom metadata */
    public var metadata: Map<String, String>? = null

    /** Whether to allow parallel tool calls */
    public var parallelToolCalls: Boolean? = null

    /** ID of a previous response to continue from */
    public var previousResponseId: String? = null

    /** Reference to a prompt template and its variables */
    public var prompt: PromptTemplate? = null

    /** Used to optimize cache hit rates; replaces the `user` field */
    public var promptCacheKey: String? = null

    /** Reasoning configuration */
    public var reasoning: ReasoningConfig? = null

    /** A stable identifier used to help detect policy violations */
    public var safetyIdentifier: String? = null

    /** Specifies the processing tier used for serving the request */
    public var serviceTier: ServiceTier? = null

    /** Whether to store the response */
    public var store: Boolean? = null

    /** Whether to stream the response */
    public var stream: Boolean? = null

    /** Sampling temperature */
    public var temperature: Double? = null

    /** Text response configuration */
    public var text: ResponseTextConfig? = null

    /** Tool choice configuration */
    public var toolChoice: ResponseToolChoiceConfig? = null

    /** Tools that the model may use */
    public var tools: MutableList<ResponseTool>? = null

    /** Number of most likely tokens to return with log probabilities */
    public var topLogprobs: Int? = null

    /** Top-p sampling parameter */
    public var topP: Double? = null

    /**
     * Truncation configuration
     * - `auto`: If the context exceeds the model's context window size, the model will truncate
     *    the response by dropping input items in the middle of the conversation.
     * - `disabled` (default): If a model response will exceed the context window size,
     *    the request will fail with a 400 error.
     */
    public var truncation: Truncation? = null

    /** End-user identifier */
    public var user: String? = null

    /** Add a tool to the request */
    public fun tool(tool: ResponseTool) {
        if (tools == null) {
            tools = mutableListOf()
        }
        tools?.add(tool)
    }

    /** Add multiple tools to the request */
    public fun tools(init: MutableList<ResponseTool>.() -> Unit) {
        if (tools == null) {
            tools = mutableListOf()
        }
        tools?.init()
    }

    /** Add an includable option */
    public fun include(includable: ResponseIncludable) {
        include = include.orEmpty() + includable
    }

    /** Build the ResponseRequest object */
    public fun build(): ResponseRequest {
        requireNotNull(model) { "Model must be set" }
        requireNotNull(input) { "Input must be set" }

        return ResponseRequest(
            input = input!!,
            model = model!!,
            background = background,
            include = include,
            instructions = instructions,
            maxOutputTokens = maxOutputTokens,
            maxToolCalls = maxToolCalls,
            metadata = metadata,
            parallelToolCalls = parallelToolCalls,
            previousResponseId = previousResponseId,
            prompt = prompt,
            promptCacheKey = promptCacheKey,
            reasoning = reasoning,
            safetyIdentifier = safetyIdentifier,
            serviceTier = serviceTier,
            store = store,
            stream = stream,
            temperature = temperature,
            text = text,
            toolChoice = toolChoice,
            tools = tools,
            topLogprobs = topLogprobs,
            topP = topP,
            truncation = truncation,
            user = user,
        )
    }
}

/** Creates a new ResponseRequest using a builder DSL */
public fun responseRequest(init: ResponseRequestBuilder.() -> Unit): ResponseRequest {
    val builder = ResponseRequestBuilder()
    builder.init()
    return builder.build()
}
