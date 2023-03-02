package com.aallam.openai.api.audio

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Create transcription response.
 *
 * Note: This does not currently support the non-default response format types.
 */
@BetaOpenAI
@Serializable
public data class Transcription(
    /**
     * Transcription text.
     */
    @SerialName("text") val text: String
)
