package com.aallam.openai.api.audio

import com.aallam.openai.api.audio.TranscriptionChunkingStrategy.Auto
import com.aallam.openai.api.audio.TranscriptionChunkingStrategy.ServerVad
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

/** Automatic or explicitly configured server-side voice activity detection. */
@Serializable(with = TranscriptionChunkingStrategySerializer::class)
public sealed interface TranscriptionChunkingStrategy {
    /** Let the server determine chunk boundaries. */
    public data object Auto : TranscriptionChunkingStrategy

    /** Configure server-side voice activity detection. */
    @Serializable
    public data class ServerVad(
        /** Audio retained before detected speech, in milliseconds. */
        @SerialName("prefix_padding_ms") public val prefixPaddingMs: Int? = null,
        /** Silence duration required to end speech, in milliseconds. */
        @SerialName("silence_duration_ms") public val silenceDurationMs: Int? = null,
        /** Voice activity sensitivity between zero and one. */
        public val threshold: Double? = null,
    ) : TranscriptionChunkingStrategy
}

internal object TranscriptionChunkingStrategySerializer : KSerializer<TranscriptionChunkingStrategy> {
    override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor
    override fun serialize(encoder: Encoder, value: TranscriptionChunkingStrategy) {
        require(encoder is JsonEncoder)
        encoder.encodeJsonElement(when (value) {
            Auto -> JsonPrimitive("auto")
            is ServerVad -> JsonObject(encoder.json.encodeToJsonElement(ServerVad.serializer(), value).jsonObject +
                ("type" to JsonPrimitive("server_vad")))
        })
    }
    override fun deserialize(decoder: Decoder): TranscriptionChunkingStrategy {
        require(decoder is JsonDecoder)
        val value = decoder.decodeJsonElement()
        if (value == JsonPrimitive("auto")) return Auto
        require(value.jsonObject["type"]?.jsonPrimitive?.content == "server_vad") { "Unknown chunking strategy" }
        return decoder.json.decodeFromJsonElement(ServerVad.serializer(), JsonObject(value.jsonObject - "type"))
    }
}

/** Token confidence information returned by transcription models. */
@Serializable
public data class AudioLogprob(
    /** Transcribed token. */
    public val token: String? = null,
    /** UTF-8 bytes of the token. */
    public val bytes: List<Int>? = null,
    /** Log probability of the token. */
    public val logprob: Double? = null,
)

/** A language detected in multilingual transcription. */
@Serializable
public data class TranscriptionLanguage(
    /** Detected language code. */
    public val code: String,
)

/** Usage for token-based or duration-based audio billing. */
@Serializable
public data class AudioUsage(
    /** Usage variant, such as tokens or duration. */
    public val type: String? = null,
    /** Billed input tokens. */
    @SerialName("input_tokens") public val inputTokens: Int? = null,
    /** Generated output tokens. */
    @SerialName("output_tokens") public val outputTokens: Int? = null,
    /** Total billed tokens. */
    @SerialName("total_tokens") public val totalTokens: Int? = null,
    /** Audio duration billed in seconds. */
    public val seconds: Double? = null,
    /** Breakdown of input tokens by modality. */
    @SerialName("input_token_details") public val inputTokenDetails: AudioInputTokenDetails? = null,
)

/** Input token breakdown for audio requests. */
@Serializable
public data class AudioInputTokenDetails(
    /** Tokens from input audio. */
    @SerialName("audio_tokens") public val audioTokens: Int? = null,
    /** Tokens from input text. */
    @SerialName("text_tokens") public val textTokens: Int? = null,
)

/** Speaker-labeled transcript segment. */
@Serializable
public data class DiarizedSegment(
    /** Segment identifier. */
    public val id: String,
    /** Speaker label or known speaker name. */
    public val speaker: String,
    /** Start timestamp in seconds. */
    public val start: Double,
    /** End timestamp in seconds. */
    public val end: Double,
    /** Transcribed text. */
    public val text: String,
    /** Segment event type. */
    @EncodeDefault public val type: String = "transcript.text.segment",
)

/** Incremental transcription event, including delta, done and speaker segment events. */
@Serializable
public data class TranscriptionEvent(
    /** Event discriminator returned by the API. */
    public val type: String,
    /** Incremental text. */
    public val delta: String? = null,
    /** Completed transcript or segment text. */
    public val text: String? = null,
    /** Token confidence information. */
    public val logprobs: List<AudioLogprob>? = null,
    /** Request usage, when returned on completion. */
    public val usage: AudioUsage? = null,
    /** Identifier linking a text delta to its speaker segment. */
    @SerialName("segment_id") public val segmentId: String? = null,
    /** Detected languages on completion. */
    public val languages: List<TranscriptionLanguage>? = null,
    /** Speaker segment identifier. */
    public val id: String? = null,
    /** Speaker label. */
    public val speaker: String? = null,
    /** Segment start in seconds. */
    public val start: Double? = null,
    /** Segment end in seconds. */
    public val end: Double? = null,
)

/** Incremental speech event containing base64 audio or completion usage. */
@Serializable
public data class SpeechEvent(
    /** Event discriminator returned by the API. */
    public val type: String,
    /** Base64-encoded audio delta. */
    public val delta: String? = null,
    /** Base64 audio payload when supplied separately from a text delta. */
    public val audio: String? = null,
    /** Request usage returned on completion. */
    public val usage: AudioUsage? = null,
)
