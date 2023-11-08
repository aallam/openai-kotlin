package com.aallam.openai.api.image

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.OpenAIDsl
import com.aallam.openai.api.file.FileSource

/**
 * Image edit request.
 */
public class ImageEdit(
    /**
     * The image to edit. Must be a valid PNG file, less than 4MB, and square.
     * If mask is not provided, image must have transparency, which will be used as the mask.
     */
    public val image: FileSource,

    /**
     * An additional [image] whose fully transparent areas (e.g. where alpha is zero) indicate where [image] should be
     * edited. Must be a valid PNG file, less than 4MB, and have the same dimensions as image.
     */
    public val mask: FileSource,

    /**
     * A text description of the desired image(s). The maximum length is 1000 characters.
     */
    public val prompt: String,

    /**
     * The model used to generate image. Must be one of dall-e-2 or dall-e-3. If not provided, dall-e-2 is used.
     */
    public val model: String? = null,

    /**
     * The number of images to generate. Must be between 1 and 10.
     */
    public val n: Int? = null,

    /**
     * The size of the generated images.
     */
    public val size: ImageSize? = null,

    /**
     * A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse.
     */
    public val user: String? = null,
)

/**
 * Image edit request.
 */
@BetaOpenAI
public fun imageEdit(block: ImageEditBuilder.() -> Unit): ImageEdit = ImageEditBuilder().apply(block).build()

/**
 * Builder of [ImageEdit] instances.
 */
@BetaOpenAI
@OpenAIDsl
public class ImageEditBuilder {
    /**
     * The image to edit. Must be a valid PNG file, less than 4MB, and square.
     * If mask is not provided, image must have transparency, which will be used as the mask.
     */
    public var image: FileSource? = null

    /**
     * An additional [image] whose fully transparent areas (e.g. where alpha is zero) indicate where [image] should be
     * edited. Must be a valid PNG file, less than 4MB, and have the same dimensions as image.
     */
    public var mask: FileSource? = null

    /**
     * A text description of the desired image(s). The maximum length is 1000 characters.
     */
    public var prompt: String? = null

    /**
     * The model used to generate image. Must be one of dall-e-2 or dall-e-3. If not provided, dall-e-2 is used.
     */
    public val model: String? = null

    /**
     * The number of images to generate. Must be between 1 and 10.
     */
    public var n: Int? = null

    /**
     * The size of the generated images.
     */
    public var size: ImageSize? = null

    /**
     * A unique identifier representing your end-user, which can help OpenAI to monitor and detect abuse.
     */
    public var user: String? = null

    /**
     * Creates the [ImageEdit] instance
     */
    public fun build(): ImageEdit = ImageEdit(
        image = requireNotNull(image) { "image field is required" },
        mask = requireNotNull(mask) { "mask field is required" },
        prompt = requireNotNull(prompt) { "prompt field is required" },
        model = model,
        n = n,
        size = size,
        user = user
    )
}
