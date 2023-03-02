package com.aallam.openai.api.image

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Generated image JSON (base 64).
 */
@BetaOpenAI
@Serializable
public data class ImageJSON(

    /**
     * Image url string.
     */
    @SerialName("b64_json")
    public val b64JSON: String
)
