package com.aallam.openai.api.audio

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Create translation response.
 *
 * [text] format depends on [TranslationRequest]'s `responseFormat`.
 * Remaining field are provided only in case of response format `verbose_json`.
 */
@Serializable
public data class Translation(
    /**
     * Translation text.
     *
     * The format depends on [TranslationRequest]'s `responseFormat`.
     */
    @SerialName("text") val text: String,
    @SerialName("language") val language: String? = null,
    @SerialName("duration") val duration: Double? = null,
    @SerialName("segments") val segments: List<Segment>? = null,
)
