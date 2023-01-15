package com.aallam.openai.api.image

/**
 * Image generation request.
 */
public data class ImageCreation(
    public val prompt: String,
    public val n: Int? = null,
    public val size: ImageSize? = null,
    public val user: String? = null
)
