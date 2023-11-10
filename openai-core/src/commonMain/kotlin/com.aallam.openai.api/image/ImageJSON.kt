package com.aallam.openai.api.image

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Generated image JSON (base 64).
 */
@Serializable
public data class ImageJSON(

    /**
     * Image url string.
     */
    @SerialName("b64_json")
    public val b64JSON: String,

    /**
     * The prompt that was used to generate the image if there was any revision to the prompt.
     */
    @SerialName("revised_prompt")
    public val revisedPrompt: String? = null,
)
