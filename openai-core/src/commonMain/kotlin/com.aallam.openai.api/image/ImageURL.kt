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
    public val url: String,

    /**
     * The prompt that was used to generate the image if there was any revision to the prompt.
     */
    @SerialName("revised_prompt")
    public val revisedPrompt: String? = null,
)
