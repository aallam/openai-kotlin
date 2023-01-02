package com.aallam.openai.api.image

import com.aallam.openai.api.ExperimentalOpenAI

/**
 * Image generation request.
 */
@ExperimentalOpenAI
public data class ImageCreation(
    public val prompt: String,
    public val n: Int? = null,
    public val size: ImageSize? = null,
    public val user: String? = null
)
