package com.aallam.openai.api.image

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.FileSource
import okio.Source

/**
 * Image edit request.
 */
@ExperimentalOpenAI
public data class ImageEdit
    public val image: FileSource,
    public val mask: FileSource,
    public val prompt: String,
    public val n: Int? = null,
    public val size: ImageSize? = null,
    public val user: String? = null,
)
