package com.aallam.openai.api.image.internal

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.InternalOpenAI
import com.aallam.openai.api.image.ImageSize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Image generation request.
 * Results are expected as URLs.
 */
@OptIn(BetaOpenAI::class)
@Serializable
@InternalOpenAI
public data class ImageCreationRequest(
    @SerialName("prompt") val prompt: String,
    @SerialName("model") val model: String?,
    @SerialName("n") val n: Int? = null,
    @SerialName("size") val size: ImageSize? = null,
    @SerialName("user") val user: String? = null,
    @SerialName("response_format") val responseFormat: ImageResponseFormat,
)
