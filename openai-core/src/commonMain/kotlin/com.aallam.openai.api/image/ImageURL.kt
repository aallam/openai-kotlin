package com.aallam.openai.api.image

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Generated image URL.
 */
@Serializable
public data class ImageURL(

    /**
     * Image url string.
     */
    @SerialName("url")
    public val url: String
)
