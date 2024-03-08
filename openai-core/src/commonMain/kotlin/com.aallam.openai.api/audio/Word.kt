package com.aallam.openai.api.audio

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class Word(
    @SerialName("word") val word: String,
    @SerialName("start") val start: Double,
    @SerialName("end") val end: Double,
)
