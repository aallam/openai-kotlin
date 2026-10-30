package com.aallam.openai.api.chat

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Prompt caching mode and minimum lifetime; supported values depend on the model. */
@Serializable
public data class ChatPromptCacheOptions(
    /** Implicit or explicit cache boundary selection. */
    @SerialName("mode") public val mode: String? = null,
    /** Minimum lifetime for cached prefixes. */
    @SerialName("ttl") public val ttl: String? = null,
)

/** Explicit cache boundary on an input content part. */
@Serializable
public data class PromptCacheBreakpoint(
    /** Boundary selection mode. */
    @EncodeDefault @SerialName("mode") public val mode: String = "explicit",
    /** Optional minimum lifetime overriding the request default. */
    @SerialName("ttl") public val ttl: String? = null,
)

/** Audio output configuration. */
@Serializable
public data class ChatAudioConfig(
    /** Audio encoding. */
    @SerialName("format") public val format: String,
    /** Built-in voice name. */
    @SerialName("voice") public val voice: String,
)

/** Predicted output content used to accelerate responses with a known prefix. */
@Serializable
public data class ChatPrediction(
    /** Predicted string or text content parts. */
    @SerialName("content") public val content: Content,
    /** Prediction kind; the API currently supports content. */
    @EncodeDefault @SerialName("type") public val type: String = "content",
) {
    /** Predict a complete text output. */
    public constructor(content: String) : this(TextContent(content))
}

/** Moderation configuration for chat inputs and outputs. */
@Serializable
public data class ChatModeration(
    /** Moderation model ID. */
    @SerialName("model") public val model: String,
    /** Policies for request input and generated output. */
    @SerialName("policy") public val policy: ChatModerationPolicy? = null,
)

/** Separate moderation policies for input and output. */
@Serializable
public data class ChatModerationPolicy(
    /** Policy applied to request input. */
    @SerialName("input") public val input: ChatModerationMode? = null,
    /** Policy applied to generated output. */
    @SerialName("output") public val output: ChatModerationMode? = null,
)

/** Moderation behavior: score content or block policy violations. */
@Serializable
public data class ChatModerationMode(
    /** Either score or block. */
    @SerialName("mode") public val mode: String,
)

/** Generated audio, or a reference to a prior assistant audio output. */
@Serializable
public data class ChatAudio(
    /** Audio output ID; sufficient to reference a prior output in a request. */
    @SerialName("id") public val id: String,
    /** Base64-encoded audio. */
    @SerialName("data") public val data: String? = null,
    /** Transcript of the audio output. */
    @SerialName("transcript") public val transcript: String? = null,
    /** Expiration time in Unix seconds. */
    @SerialName("expires_at") public val expiresAt: Long? = null,
)
