package com.aallam.openai.api.image

import com.aallam.openai.api.file.FileSource

/**
 * Image edit request.
 */
public data class ImageEdit(
    public val image: FileSource,
    public val mask: FileSource,
    public val prompt: String,
    public val n: Int? = null,
    public val size: ImageSize? = null,
    public val user: String? = null,
)
