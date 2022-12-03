package com.aallam.openai.api.image

import com.aallam.openai.api.ExperimentalOpenAI
import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Image generation request.
 * Results are expected as URLs.
 */
@ExperimentalOpenAI
@Serializable
public data class ImageCreationURL(
    @SerialName("prompt") public override val prompt: String,
    @SerialName("n") public override val n: Int? = null,
    @SerialName("size") public override val size: ImageSize? = null,
    @SerialName("user") public override val user: String? = null
) : ImageRequestPrompt {

    /** The format in which the generated images are returned. */
    @SerialName("response_format")
    @EncodeDefault
    public val responseFormat: ResponseFormat = ResponseFormat.url
}


/**
 * Image generation request.
 * Results are expected as base 64 JSONs.
 */
@ExperimentalOpenAI
@Serializable
public data class ImageCreationJSON(
    @SerialName("prompt") public override val prompt: String,
    @SerialName("n") public override val n: Int? = null,
    @SerialName("size") public override val size: ImageSize? = null,
    @SerialName("user") public override val user: String? = null
) : ImageRequestPrompt {

    /** The format in which the generated images are returned. */
    @SerialName("response_format")
    @EncodeDefault
    public val responseFormat: ResponseFormat  = ResponseFormat.base64Json
}
