package com.aallam.openai.api.audio

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Create transcription response.
 *
 * [text] format depends on [TranscriptionRequest]'s `responseFormat`.
 * Remaining field are provided only in case of response format `verbose_json`.
 */
@Serializable
public data class Transcription(
    /**
     * Transcription [text].
     * The format depends on [TranscriptionRequest]'s `responseFormat`.
     */
    @SerialName("text") val text: String,
    @SerialName("language") val language: String? = null,
    @SerialName("duration") val duration: Double? = null,
    @SerialName("segments") val segments: List<Segment>? = null,
    @SerialName("words") val words: List<Word>? = null,
)
