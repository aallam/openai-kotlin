package com.aallam.openai.api.image

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ImageRequestURL(
    /**
     * A text description of the desired image(s). The maximum length is 1000 characters.
     */
    @SerialName("prompt") public val prompt: String,

    /**
     * The number of images to generate. Must be between 1 and 10.
     */
    @SerialName("n") public val n: Int? = null,

    /**
     * The size of the generated images. Must be one of 256x256, 512x512, or 1024x1024.
     */
    @SerialName("size") public val size: ImageSize? = null,

    /**
     * A unique identifier representing your end-user, which will help OpenAI to monitor and detect abuse.
     */
    @SerialName("user") public val user: String? = null
) {

    /**
     * The format in which the generated images are returned. Must be one of url or b64_json.
     */
    @EncodeDefault
    @SerialName("response_format")
    public val responseFormat: ResponseFormat = ResponseFormat.url
}

@Serializable
public data class ImageRequestJSON(
    /**
     * A text description of the desired image(s). The maximum length is 1000 characters.
     */
    @SerialName("prompt") public val prompt: String,

    /**
     * The number of images to generate. Must be between 1 and 10.
     */
    @SerialName("n") public val n: Int? = null,

    /**
     * The size of the generated images. Must be one of 256x256, 512x512, or 1024x1024.
     */
    @SerialName("size") public val size: ImageSize? = null,

    /**
     * A unique identifier representing your end-user, which will help OpenAI to monitor and detect abuse.
     */
    @SerialName("user") public val user: String? = null
) {

    /**
     * The format in which the generated images are returned. Must be one of url or b64_json.
     */
    @EncodeDefault
    @SerialName("response_format")
    public val responseFormat: ResponseFormat = ResponseFormat.base64Json
}
