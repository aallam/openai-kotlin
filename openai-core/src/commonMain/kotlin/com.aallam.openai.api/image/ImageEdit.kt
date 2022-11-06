package com.aallam.openai.api.image

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.common.FilePath

/**
 * Image edit request.
 */
@ExperimentalOpenAI
public interface ImageEdit : ImageRequestPrompt {
    /**
     * The image to edit. Must be a valid PNG file, less than 4MB, and square.
     */
    public val image: FilePath

    /**
     * An additional image whose fully transparent areas (e.g. where alpha is zero) indicate where [image] should be
     * edited. Must be a valid PNG file, less than 4MB, and have the same dimensions as [image].
     */
    public val mask: FilePath
}

/**
 * Image edit request.
 * Results are expected as URLs.
 */
@ExperimentalOpenAI
public data class ImageEditURL(
    override val image: FilePath,
    override val mask: FilePath,
    override val prompt: String,
    override val n: Int? = null,
    override val size: ImageSize? = null,
    override val user: String? = null,
) : ImageEdit

/**
 * Image edit request.
 * Results are expected as base 64 JSONs.
 */
@ExperimentalOpenAI
public data class ImageEditJSON(
    override val image: FilePath,
    override val mask: FilePath,
    override val prompt: String,
    override val n: Int? = null,
    override val size: ImageSize? = null,
    override val user: String? = null,
) : ImageEdit
