package com.aallam.openai.api.audio

import kotlinx.serialization.SerialName
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

/**
 * Modern transcription result with model-specific confidence, usage and speaker details.
 *
 * Timestamp and speaker-labeled segments are represented separately.
 */
@Serializable(with = AudioTranscriptionSerializer::class)
public data class AudioTranscription(
    /**
     * Transcription [text].
     * The format depends on [TranscriptionRequest]'s `responseFormat`.
     */
    @SerialName("text") val text: String,
    @SerialName("language") val language: String? = null,
    @SerialName("duration") val duration: Double? = null,
    @SerialName("segments") val segments: List<Segment>? = null,
    @SerialName("words") val words: List<Word>? = null,
    /** Token confidence information when requested. */
    public val logprobs: List<AudioLogprob>? = null,
    /** Languages detected by multilingual models. */
    public val languages: List<TranscriptionLanguage>? = null,
    /** Token-based or duration-based request usage. */
    public val usage: AudioUsage? = null,
    /** Speaker-labeled segments for diarized JSON responses. */
    public val diarizedSegments: List<DiarizedSegment>? = null,
    /** Task name returned by diarized responses. */
    public val task: String? = null,
)

@Serializable
private data class AudioTranscriptionPayload(
    val text: String,
    val language: String? = null,
    val duration: Double? = null,
    val segments: JsonArray? = null,
    val words: List<Word>? = null,
    val logprobs: List<AudioLogprob>? = null,
    val languages: List<TranscriptionLanguage>? = null,
    val usage: AudioUsage? = null,
    val task: String? = null,
)

internal object AudioTranscriptionSerializer : KSerializer<AudioTranscription> {
    override val descriptor: SerialDescriptor = AudioTranscriptionPayload.serializer().descriptor

    override fun deserialize(decoder: Decoder): AudioTranscription {
        require(decoder is JsonDecoder)
        val payload = decoder.decodeSerializableValue(AudioTranscriptionPayload.serializer())
        val first = payload.segments?.firstOrNull()?.jsonObject
        val diarized = first?.containsKey("speaker") == true || (first == null && payload.task == "transcribe")
        return AudioTranscription(
            text = payload.text, language = payload.language, duration = payload.duration,
            segments = if (diarized) null else payload.segments?.map { decoder.json.decodeFromJsonElement<Segment>(it) },
            words = payload.words, logprobs = payload.logprobs, languages = payload.languages,
            usage = payload.usage, task = payload.task,
            diarizedSegments = if (diarized) payload.segments?.map { decoder.json.decodeFromJsonElement<DiarizedSegment>(it) } else null,
        )
    }

    override fun serialize(encoder: Encoder, value: AudioTranscription) {
        require(encoder is JsonEncoder)
        require(value.segments == null || value.diarizedSegments == null) { "Specify timestamp segments or diarized segments, not both" }
        val segments = value.diarizedSegments?.map { encoder.json.encodeToJsonElement(it) }
            ?: value.segments?.map { encoder.json.encodeToJsonElement(it) }
        encoder.encodeSerializableValue(AudioTranscriptionPayload.serializer(), AudioTranscriptionPayload(
            value.text, value.language, value.duration, segments?.let { JsonArray(it) }, value.words,
            value.logprobs, value.languages, value.usage, value.task,
        ))
    }
}
