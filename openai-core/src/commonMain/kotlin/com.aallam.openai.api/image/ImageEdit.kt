package com.aallam.openai.api.image

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.OpenAIDsl
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.model.ModelId

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
     * A text description of the desired image(s).
     * Maximum length: 32000 characters for GPT image models, 4000 for dall-e-3, 1000 for dall-e-2.
     */
    public val prompt: String,

    /**
     * The model to use for image generation.
     * One of dall-e-2, dall-e-3, or a GPT image model (gpt-image-1, gpt-image-1-mini, gpt-image-1.5).
     */
    public val model: ModelId? = null,

    /**
     * The number of images to generate. Must be between 1 and 10.
     */
    public val n: Int? = null,

    /**
     * The size of the generated images.
     */
    public val size: ImageSize? = null,

    /**
     * The quality of the image that will be generated.
     */
    public val quality: Quality? = null,

    /**
     * Allows to set transparency for the background of the generated image(s).
     * This parameter is only supported for the GPT image models.
     */
    public val background: Background? = null,

    /**
     * Control how much effort the model will exert to match the style and features,
     * especially facial features, of input images.
     * This parameter is only supported for gpt-image-1
     */
    public val inputFidelity: InputFidelity? = null,

    /**
     * The format in which the generated images are returned.
     * This parameter is only supported for the GPT image models.
     */
    public val outputFormat: OutputFormat? = null,

    /**
     * The compression level (0-100%) for the generated images.
     * This parameter is only supported for the GPT image models with the webp or jpeg output formats.
     */
    public val outputCompression: Int? = null,

    /**
     * The number of partial images to generate (0-3).
     * This parameter is used for streaming responses that return partial images.
     */
    public val partialImages: Int? = null,

    /**
     * The format in which generated images with dall-e-2 and dall-e-3 are returned.
     * Must be one of url or b64_json.
     */
    public val responseFormat: String? = null,

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
     * A text description of the desired image(s).
     * Maximum length: 32000 characters for GPT image models, 4000 for dall-e-3, 1000 for dall-e-2.
     */
    public var prompt: String? = null

    /**
     * The model to use for image generation.
     */
    public var model: ModelId? = null

    /**
     * The number of images to generate. Must be between 1 and 10.
     */
    public var n: Int? = null

    /**
     * The size of the generated images.
     */
    public var size: ImageSize? = null

    /**
     * The quality of the image that will be generated.
     */
    public var quality: Quality? = null

    /**
     * Allows to set transparency for the background of the generated image(s).
     */
    public var background: Background? = null

    /**
     * Control how much effort the model will exert to match the style and features of input images.
     */
    public val inputFidelity: InputFidelity? = null

    /**
     * The format in which the generated images are returned.
     */
    public var outputFormat: OutputFormat? = null

    /**
     * The compression level (0-100%) for the generated images.
     */
    public var outputCompression: Int? = null

    /**
     * The number of partial images to generate (0-3).
     */
    public var partialImages: Int? = null

    /**
     * The format in which generated images with dall-e-2 and dall-e-3 are returned.
     */
    public var responseFormat: String? = null

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
        quality = quality,
        background = background,
        inputFidelity = inputFidelity,
        outputFormat = outputFormat,
        outputCompression = outputCompression,
        partialImages = partialImages,
        responseFormat = responseFormat,
        user = user,
    )
}
