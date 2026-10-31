package com.aallam.openai.api.realtime

import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.core.Parameters
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject

/** GA Realtime or transcription session configuration. Model-dependent controls remain optional. */
@Serializable
public data class RealtimeSessionConfig(
    /** realtime or transcription. */
    @EncodeDefault public val type: String = "realtime",
    /** Model used for a conversation session. */
    public val model: ModelId? = null,
    /** System instructions. */
    public val instructions: String? = null,
    /** Input and output audio configuration. */
    public val audio: RealtimeAudioConfig? = null,
    /** Output modalities, typically audio or text. */
    @SerialName("output_modalities") public val outputModalities: List<String>? = null,
    /** Maximum response tokens, or the JSON string inf. */
    @SerialName("max_output_tokens") public val maxOutputTokens: JsonElement? = null,
    /** Functions/MCP tools. Raw tool objects retain model-specific variants. */
    public val tools: List<JsonObject>? = null,
    /** Tool selection mode or named-tool object. */
    @SerialName("tool_choice") public val toolChoice: JsonElement? = null,
    /** Whether parallel tool calls are allowed. */
    @SerialName("parallel_tool_calls") public val parallelToolCalls: Boolean? = null,
    /** Additional response fields to include. */
    public val include: List<String>? = null,
    /** Reasoning controls for supporting models. */
    public val reasoning: JsonObject? = null,
    /** Trace configuration or the string auto. */
    public val tracing: JsonElement? = null,
    /** Reusable prompt configuration, where still supported. */
    public val prompt: JsonObject? = null,
    /** Context truncation controls. */
    public val truncation: JsonElement? = null,
)

/** Audio configuration for GA Realtime sessions. */
@Serializable
public data class RealtimeAudioConfig(
    /** Incoming audio configuration. */
    public val input: RealtimeAudioInput? = null,
    /** Outgoing audio configuration. */
    public val output: RealtimeAudioOutput? = null,
)

/** Incoming audio format, transcription and detection. */
@Serializable
public data class RealtimeAudioInput(
    /** PCM or G.711 audio format. */
    public val format: RealtimeAudioFormat? = null,
    /** Noise reduction configuration, or JSON null to disable it. */
    @SerialName("noise_reduction") public val noiseReduction: JsonElement? = null,
    /** Transcription configuration. */
    public val transcription: JsonElement? = null,
    /** VAD configuration, or JSON null to disable automatic turn detection. */
    @SerialName("turn_detection") public val turnDetection: JsonElement? = null,
)

/** Outgoing voice and format configuration. */
@Serializable
public data class RealtimeAudioOutput(
    /** PCM or G.711 output format. */
    public val format: RealtimeAudioFormat? = null,
    /** Built-in voice string or custom voice object. */
    public val voice: JsonElement? = null,
    /** Playback speech speed. */
    public val speed: Double? = null,
)

/** Audio media type; PCM uses 24000 Hz. */
@Serializable
public data class RealtimeAudioFormat(
    /** audio/pcm, audio/pcmu or audio/pcma. */
    public val type: String = "audio/pcm",
    /** Sample rate for PCM. */
    public val rate: Int? = null,
)

/** Function declaration for a Realtime session. */
@Serializable
public data class RealtimeFunctionTool(
    /** Function name. */
    public val name: String,
    /** JSON Schema accepted by the function. */
    public val parameters: Parameters? = null,
    /** Function description. */
    public val description: String? = null,
    /** Realtime functions use the function discriminator. */
    @EncodeDefault public val type: String = "function",
)

/** Short-lived client-secret expiry. */
@Serializable
public data class RealtimeSecretExpiry(
    /** Secret lifetime in seconds, from 10 to 7200. */
    public val seconds: Int = 600,
    /** Expiry anchor. */
    public val anchor: String = "created_at",
) {
    init { require(seconds in 10..7200) { "Client secret expiry must be between 10 and 7200 seconds" } }
}

/** Creates an ephemeral GA Realtime credential. */
@Serializable
public data class RealtimeClientSecretRequest(
    /** Session defaults for the credential. */
    public val session: RealtimeSessionConfig? = null,
    /** Credential lifetime. */
    @SerialName("expires_after") public val expiresAfter: RealtimeSecretExpiry? = null,
)

/** Ephemeral credential. Do not log or persist its value. */
@Serializable
public class RealtimeClientSecret(
    /** Bearer credential for a client connection. */
    public val value: String,
    /** Expiry Unix timestamp in seconds. */
    @SerialName("expires_at") public val expiresAt: Long,
    /** Effective session configuration, preserved without dropping server fields. */
    public val session: JsonObject,
) {
    override fun toString(): String = "RealtimeClientSecret(value=<redacted>, expiresAt=$expiresAt)"
}

/** Continuous translation configuration. Input is PCM16 mono little-endian audio at 24kHz over WebSocket. */
@Serializable
public data class RealtimeTranslationSessionConfig(
    /** Translation model; cannot be changed after session creation. */
    public val model: ModelId,
    /** Translation-specific input noise-reduction/transcription and output language configuration. */
    public val audio: JsonObject? = null,
)

/** Creates an ephemeral translation credential. */
@Serializable
public data class RealtimeTranslationSecretRequest(
    /** Translation configuration. */
    public val session: RealtimeTranslationSessionConfig,
    /** Credential lifetime. */
    @SerialName("expires_after") public val expiresAfter: RealtimeSecretExpiry? = null,
)

/** WebRTC SDP answer with the call location used for later control operations. */
public data class RealtimeCall(
    /** SDP answer to supply to the caller's peer connection. */
    public val sdp: String,
    /** Location header identifying the created call, when supplied. */
    public val location: String? = null,
) {
    /** Call identifier parsed from its Location header. */
    public val id: String? get() = location?.substringBefore('?')?.substringAfterLast('/')?.takeIf { it.isNotEmpty() }
}
