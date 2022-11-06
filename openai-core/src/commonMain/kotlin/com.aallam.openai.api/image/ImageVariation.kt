package com.aallam.openai.api.image

/**
 * Image variant request.
 */
public interface ImageVariation {
    /**
     * The image to edit. Must be a valid PNG file, less than 4MB, and square.
     */
    public val image: String

    /**
     * The number of images to generate. Must be between 1 and 10.
     */
    public val n: Int?

    /**
     * The size of the generated images. Must be one of 256x256, 512x512, or 1024x1024.
     */
    public val size: ImageSize?

    /**
     * A unique identifier representing your end-user, which will help OpenAI to monitor and detect abuse.
     */
    public val user: String?
}

/**
 * Image variant request.
 * Results are expected as URLs.
 */
public data class ImageVariationURL(
    override val image: String,
    override val n: Int? = null,
    override val size: ImageSize? = null,
    override val user: String? = null,
) : ImageVariation

/**
 * Image variant request.
 * Results are expected as base 64 JSONs.
 */
public data class ImageVariationJSON(
    override val image: String,
    override val n: Int? = null,
    override val size: ImageSize? = null,
    override val user: String? = null,
) : ImageVariation
