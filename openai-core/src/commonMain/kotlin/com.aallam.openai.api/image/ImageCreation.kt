package com.aallam.openai.api.image

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.OpenAIDsl
import com.aallam.openai.api.model.ModelId

/**
 * Image generation request.
 */
public class ImageCreation(
    /**
     * A text description of the desired image(s). The maximum length is 1000 characters.
     */
    public val prompt: String,
    /**
     * The model used to generate image. Must be one of dall-e-2 or dall-e-3. If not provided, dall-e-2 is used.
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
     * The format in which the generated images are returned. Must be one of url or b64_json.
     */
    public val user: String? = null
)

/**
 * Image generation request.
 */
@BetaOpenAI
public fun imageCreation(block: ImageCreationBuilder.() -> Unit): ImageCreation = ImageCreationBuilder().apply(block).build()

/**
 * Builder of [ImageCreation] instances.
 */
@BetaOpenAI
@OpenAIDsl
public class ImageCreationBuilder {

    /**
     * A text description of the desired image(s). The maximum length is 1000 characters.
     */
    public var prompt: String? = null

    /**
     * The model used to generate image. Must be one of dall-e-2 or dall-e-3. If not provided, dall-e-2 is used.
     */
    public val model: ModelId? = null

    /**
     * The number of images to generate. Must be between 1 and 10.
     */
    public var n: Int? = null

    /**
     * The size of the generated images.
     */
    public var size: ImageSize? = null

    /**
     * The format in which the generated images are returned. Must be one of url or b64_json.
     */
    public var user: String? = null

    /**
     * Creates the [ImageCreation] instance
     */
    public fun build(): ImageCreation = ImageCreation(
        prompt = requireNotNull(prompt),
        model = model,
        n = n,
        size = size,
        user = user,
    )
}
