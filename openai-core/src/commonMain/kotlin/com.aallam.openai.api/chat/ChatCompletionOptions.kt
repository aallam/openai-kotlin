package com.aallam.openai.api.chat

import kotlinx.serialization.*

/** Advanced fields whose types differ from the original Chat request contract. */
@Serializable
public data class ChatCompletionOptions(
    /** Replaces request messages with richer multimodal messages. */
    public val messages: List<ChatInputMessage>? = null,
    /** Replaces request tools, allowing custom text tools. */
    public val tools: List<ChatTool>? = null,
    /** Replaces the original function-only tool choice. */
    @SerialName("tool_choice") public val toolChoice: ChatToolSelection? = null,
    /** Key-value metadata attached to a stored completion. */
    @SerialName("metadata") public val metadata: Map<String, String>? = null,
    /** Allow multiple tool calls in a single model turn. */
    @SerialName("parallel_tool_calls") public val parallelToolCalls: Boolean? = null,
    /** Requested processing tier. */
    @SerialName("service_tier") public val serviceTier: String? = null,
    /** Stable end-user identifier used for abuse detection. */
    @SerialName("safety_identifier") public val safetyIdentifier: String? = null,
    /** Cache routing key for related prompts. */
    @SerialName("prompt_cache_key") public val promptCacheKey: String? = null,
    /** Prompt caching behavior. */
    @SerialName("prompt_cache_options") public val promptCacheOptions: ChatPromptCacheOptions? = null,
    /** Requested detail level in generated text. */
    @SerialName("verbosity") public val verbosity: String? = null,
    /** Moderation configuration. */
    @SerialName("moderation") public val moderation: ChatModeration? = null,
    /** Requested output modalities, such as text and audio. */
    @SerialName("modalities") public val modalities: List<String>? = null,
    /** Audio output voice and format. */
    @SerialName("audio") public val audio: ChatAudioConfig? = null,
    /** Predicted output content that can reduce generation latency. */
    @SerialName("prediction") public val prediction: ChatPrediction? = null,
    /** Streaming usage and obfuscation settings. */
    @SerialName("stream_options") public val streamOptions: ChatStreamOptions? = null,
)

/** Select a custom tool or an allowed tool subset. */
@Serializable
public data class ChatToolSelection(
    /** custom or allowed_tools. */
    public val type: String,
    /** Named custom tool. */
    public val custom: FunctionToolChoice? = null,
    /** Restricted selection policy. */
    @SerialName("allowed_tools") public val allowedTools: ChatAllowedTools? = null,
)

/** Allowed subset of tools. */
@Serializable
public data class ChatAllowedTools(
    /** auto or required. */
    public val mode: String,
    /** Tool references including their names. */
    public val tools: List<ChatAllowedTool>,
)

/** A function or custom tool reference. */
@Serializable
public data class ChatAllowedTool(
    /** function or custom. */
    public val type: String,
    /** Function tool reference. */
    public val function: FunctionToolChoice? = null,
    /** Custom tool reference. */
    public val custom: FunctionToolChoice? = null,
)

/** Streaming controls for advanced Chat requests. */
@Serializable
public data class ChatStreamOptions(
    /** Emit final usage details. */
    @SerialName("include_usage") public val includeUsage: Boolean? = null,
    /** Include payload-size obfuscation padding. */
    @SerialName("include_obfuscation") public val includeObfuscation: Boolean? = null,
)
