package com.aallam.openai.api.image

import com.aallam.openai.api.ExperimentalOpenAI
import okio.Source

/**
 * Image variant request.
 */
@ExperimentalOpenAI
public data class ImageVariation(
    public val filename: String,
    public val source: Source,
    public val n: Int? = null,
    public val size: ImageSize? = null,
    public val user: String? = null,
)
