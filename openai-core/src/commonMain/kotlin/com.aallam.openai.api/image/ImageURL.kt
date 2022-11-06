package com.aallam.openai.api.image

import com.aallam.openai.api.ExperimentalOpenAI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Generated image URL.
 */
@ExperimentalOpenAI
@Serializable
public data class ImageURL(

    /**
     * Image url string.
     */
    @SerialName("url")
    public val url: String
)
