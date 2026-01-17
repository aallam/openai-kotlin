package com.aallam.openai.api.image.internal

import com.aallam.openai.api.InternalOpenAI
import com.aallam.openai.api.image.*
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Image generation request for the GPT image class of models
 */
@Serializable
@InternalOpenAI
public data class GptImageCreationRequest(
    @SerialName("prompt") val prompt: String,
    @SerialName("background") val background: Background? = null,
    @SerialName("model") val model: String? = null,
    @SerialName("moderation") val moderation: Moderation? = null,
    @SerialName("n") val n: Int? = null,
    @SerialName("output_compression") val outputCompression: Int? = null,
    @SerialName("output_format") val outputFormat: OutputFormat? = null,
    @SerialName("partial_images") val partialImages: Int? = null,
    @SerialName("quality") val quality: Quality? = null,
    @SerialName("size") val size: ImageSize? = null,
    @SerialName("stream") val stream: Boolean? = null,
    @SerialName("user") val user: String? = null,
)
