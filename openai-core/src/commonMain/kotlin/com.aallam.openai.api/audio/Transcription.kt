package com.aallam.openai.api.audio

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Create transcription response.
 *
 * [text] format depends on [TranscriptionRequest]'s `responseFormat`.
 * Remaining field are provided only in case of response format `verbose_json`.
 */
@BetaOpenAI
@Serializable
public data class Transcription(
    /**
     * Transcription [text].
     * The format depends on [TranscriptionRequest]'s `responseFormat`.
     */
    @SerialName("text") val text: String,
    @SerialName("language") val language: String? = null,
    @SerialName("duration") val duration: Double? = null,
    @SerialName("segments") val segments: List<TranscriptionSegment>? = null,
)

@BetaOpenAI
@Serializable
public data class TranscriptionSegment(
    @SerialName("id") val id: Int,
    @SerialName("seek") val seek: Int,
    @SerialName("start") val start: Double,
    @SerialName("end") val end: Double,
    @SerialName("text") val text: String,
    @SerialName("tokens") val tokens: List<Int>,
    @SerialName("temperature") val temperature: Double,
    @SerialName("avg_logprob") val avgLogprob: Double,
    @SerialName("compression_ratio") val compressionRatio: Double,
    @SerialName("no_speech_prob") val noSpeechProb: Double,
    @SerialName("transient") val transient: Boolean,
)
