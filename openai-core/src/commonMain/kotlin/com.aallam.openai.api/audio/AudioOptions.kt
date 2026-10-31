package com.aallam.openai.api.audio

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Additional controls for modern transcription models. */
public data class TranscriptionOptions(
    /** Audio chunking policy. */
    public val chunkingStrategy: TranscriptionChunkingStrategy? = null,
    /** Additional response fields, such as logprobs. */
    public val include: List<String>? = null,
    /** Words to bias transcription toward. */
    public val keywords: List<String>? = null,
    /** Names corresponding to known speaker reference samples. */
    public val knownSpeakerNames: List<String>? = null,
    /** Base64 data URLs for known speaker audio samples. */
    public val knownSpeakerReferences: List<String>? = null,
    /** Expected language codes for multilingual transcription. */
    public val languages: List<String>? = null,
)

/** Additional controls for modern speech models. */
@Serializable
public data class SpeechOptions(
    /** Voice delivery instructions; model support varies. */
    public val instructions: String? = null,
    /** Response framing; streaming methods choose their required framing. */
    @SerialName("stream_format") public val streamFormat: SpeechStreamFormat? = null,
    /** Custom voice reference, replacing the request's built-in voice when supplied. */
    @SerialName("voice") public val customVoice: CustomVoice? = null,
)

/** Custom voice created from an audio sample. */
@Serializable
public data class CustomVoice(
    /** Identifier returned by the custom voice API. */
    public val id: String,
)

/** Response framing for speech delivery. */
@Serializable
public enum class SpeechStreamFormat {
    /** Incremental binary audio. */
    @SerialName("audio") Audio,
    /** Server-sent audio events. */
    @SerialName("sse") Sse,
}
