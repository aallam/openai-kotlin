package com.aallam.openai.api.audio

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@BetaOpenAI
@Serializable
public data class Segment(
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
    @SerialName("transient") val transient: Boolean? = null,
)
