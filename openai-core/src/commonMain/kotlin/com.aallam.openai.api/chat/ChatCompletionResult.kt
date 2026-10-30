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
    public val usage: Usage? = null,
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
    public val annotations: List<ChatAnnotation>? = null,
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
