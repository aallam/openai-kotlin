package com.aallam.openai.api.image

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.file.FilePath

/**
 * Image variant request.
 */
@ExperimentalOpenAI
public interface ImageVariation : ImageRequest {
    /**
     * The image to edit. Must be a valid PNG file, less than 4MB, and square.
     */
    public val image: FilePath
}

/**
 * Image variant request.
 * Results are expected as URLs.
 */
@ExperimentalOpenAI
public data class ImageVariationURL(
    override val image: FilePath,
    override val n: Int? = null,
    override val size: ImageSize? = null,
    override val user: String? = null,
) : ImageVariation

/**
 * Image variant request.
 * Results are expected as base 64 JSONs.
 */
@ExperimentalOpenAI
public data class ImageVariationJSON(
    override val image: FilePath,
    override val n: Int? = null,
    override val size: ImageSize? = null,
    override val user: String? = null,
) : ImageVariation
