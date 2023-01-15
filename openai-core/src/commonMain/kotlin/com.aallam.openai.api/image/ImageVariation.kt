package com.aallam.openai.api.image

import com.aallam.openai.api.file.FileSource

/**
 * Image variant request.
 */
public data class ImageVariation(
    public val image: FileSource,
    public val n: Int? = null,
    public val size: ImageSize? = null,
    public val user: String? = null,
)
