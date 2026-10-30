package com.aallam.openai.api.chat

import com.aallam.openai.api.core.*
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.*

/** Completion result supporting custom calls without extending legacy sealed types. */
@Serializable
public data class ChatCompletionResult(
    /** Completion ID. */
    public val id: String,
    /** Creation time in Unix seconds. */
    public val created: Long,
    /** Model ID. */
    public val model: ModelId,
    /** Generated choices. */
    public val choices: List<ChatResultChoice>,
    /** Detailed token usage. */
    public val usage: ChatUsage? = null,
    /** Effective processing tier. */
    @SerialName("service_tier") public val serviceTier: String? = null,
    /** Backend fingerprint. */
    @SerialName("system_fingerprint") public val systemFingerprint: String? = null,
    /** Metadata attached to a stored completion. */
    public val metadata: Map<String, String>? = null,
)

/** Generated choice, or a streamed choice delta. */
@Serializable
public data class ChatResultChoice(
    /** Choice index. */
    public val index: Int,
    /** Complete generated message. */
    public val message: ChatResultMessage? = null,
    /** Incremental generated message. */
    public val delta: ChatResultMessage? = null,
    /** Generation stop reason. */
    @SerialName("finish_reason") public val finishReason: FinishReason? = null,
    /** Generated token probabilities. */
    public val logprobs: Logprobs? = null,
)

/** Complete or incremental assistant message with custom tool calls. */
@Serializable
public data class ChatResultMessage(
    /** Stored message ID, when returned by message listing. */
    public val id: String? = null,
    /** Participant name returned for stored messages. */
    public val name: String? = null,
    /** Structured content returned when stored message content is null. */
    @SerialName("content_parts") public val contentParts: List<ChatInputPart>? = null,
    /** Message role. */
    public val role: Role? = null,
    /** Output text. */
    public val content: String? = null,
    /** Function and custom calls. */
    @SerialName("tool_calls") public val toolCalls: List<ChatResultToolCall>? = null,
    /** Output citations. */
    public val annotations: List<ChatResultAnnotation>? = null,
    /** Audio output. */
    public val audio: ChatAudio? = null,
    /** Refusal explanation. */
    public val refusal: String? = null,
    /** Legacy function output when requested alongside advanced options. */
    @SerialName("function_call") public val functionCall: FunctionCall? = null,
    /** Additional reasoning text from compatible providers. */
    @SerialName("reasoning_content") public val reasoningContent: String? = null,
)

/** Function/custom call; optional fields support incremental streaming updates. */
@Serializable
public data class ChatResultToolCall(
    /** Call ID. */
    public val id: String? = null,
    /** Call kind. */
    public val type: String? = null,
    /** Streaming call index. */
    public val index: Int? = null,
    /** Function call payload. */
    public val function: FunctionCall? = null,
    /** Custom call payload. */
    public val custom: ChatCustomCall? = null,
)

/** Name and free-form input of a custom call. */
@Serializable
public data class ChatCustomCall(
    /** Tool name. */
    public val name: String? = null,
    /** Tool input. */
    public val input: String? = null,
)

/** Usage details returned by advanced Chat operations. */
@Serializable
public data class ChatUsage(
    /** Prompt token count. */
    @SerialName("prompt_tokens") public val promptTokens: Int? = null,
    /** Completion token count. */
    @SerialName("completion_tokens") public val completionTokens: Int? = null,
    /** Total token count. */
    @SerialName("total_tokens") public val totalTokens: Int? = null,
    /** Cached and audio prompt token counts. */
    @SerialName("prompt_tokens_details") public val promptTokensDetails: PromptTokensDetails? = null,
    /** Reasoning, audio and prediction token counts. */
    @SerialName("completion_tokens_details") public val completionTokensDetails: CompletionTokensDetails? = null,
)

/** Output citation used by rich Chat result messages. */
@Serializable
public data class ChatResultAnnotation(
    /** Citation kind. */
    public val type: String,
    /** URL citation returned by Chat Completions. */
    @SerialName("url_citation") public val urlCitation: UrlCitation? = null,
    /** Flat URL for compatible providers. */
    public val url: String? = null,
    /** Flat source title. */
    public val title: String? = null,
    /** Start character offset. */
    @SerialName("start_index") public val startIndex: Int? = null,
    /** End character offset. */
    @SerialName("end_index") public val endIndex: Int? = null,
    /** Citation text. */
    public val text: String? = null,
    /** Referenced file ID. */
    @SerialName("file_id") public val fileId: String? = null,
    /** Referenced file name. */
    public val filename: String? = null,
    /** File citation position. */
    public val index: Int? = null,
    /** Container holding a cited file. */
    @SerialName("container_id") public val containerId: String? = null,
)

/** Nested Chat URL citation. */
@Serializable
public data class UrlCitation(
    /** Cited URL. */
    public val url: String,
    /** Source title. */
    public val title: String,
    /** First cited character. */
    @SerialName("start_index") public val startIndex: Int,
    /** End of cited span. */
    @SerialName("end_index") public val endIndex: Int,
)
