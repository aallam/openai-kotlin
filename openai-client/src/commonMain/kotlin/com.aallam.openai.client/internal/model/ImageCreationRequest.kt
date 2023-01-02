package com.aallam.openai.client.internal.model

import com.aallam.openai.api.image.ImageCreation
import com.aallam.openai.api.image.ImageSize
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Image generation request.
 * Results are expected as URLs.
 */
@Serializable
internal data class ImageCreationRequest(
    @SerialName("prompt") val prompt: String,
    @SerialName("n") val n: Int? = null,
    @SerialName("size") val size: ImageSize? = null,
    @SerialName("user") val user: String? = null,
    @SerialName("response_format") val responseFormat: ImageResponseFormat,
)

/** Convert [ImageCreation] instance to base64 JSON request */
internal fun ImageCreation.toJSONRequest() = ImageCreationRequest(
    prompt = prompt, n = n, size = size, user = user, responseFormat = ImageResponseFormat.base64Json,
)

/** Convert [ImageCreation] instance to URL request */
internal fun ImageCreation.toURLRequest() = ImageCreationRequest(
    prompt = prompt, n = n, size = size, user = user, responseFormat = ImageResponseFormat.url,
)
