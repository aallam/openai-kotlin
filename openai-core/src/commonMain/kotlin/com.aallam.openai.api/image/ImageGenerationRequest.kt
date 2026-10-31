package com.aallam.openai.api.image

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.OpenAIDsl
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Image generation request.
 */
@Serializable
public data class ImageGenerationRequest(
    /**
     * A text description of the desired image(s). GPT Image accepts up to 32000 characters.
     */
    @SerialName("prompt") public val prompt: String,
    /**
     * The number of images to generate. Must be between 1 and 10.
     */
    @SerialName("n") public val n: Int? = null,
    /**
     * The size of the generated images.
     */
    @SerialName("size") public val size: ImageSize? = null,

    /**
     * Deprecated end-user identifier; prefer safetyIdentifier.
     */
    @SerialName("user") public val user: String? = null,

    /**
     * The image model. Supply an explicit current GPT Image model for generation.
     */
    @SerialName("model") public val model: ModelId,

    /**
     * Image quality. Supported values depend on the chosen model.
     */
    @SerialName("quality") public val quality: Quality? = null,

    /**
     * The style of the generated images. Must be one of [Style.Vivid] or `[Style.Natural]`. Vivid causes the model to
     * lean towards generating hyper-real and dramatic images. Natural causes the model to produce more natural, less
     * hyper-real looking images. This param is only supported for dall-e-3.
     */
    @SerialName("style") public val style: Style? = null,
    /** Background transparency. */
    @SerialName("background") public val background: ImageBackground? = null,
    /** Image moderation level. */
    @SerialName("moderation") public val moderation: ImageModeration? = null,
    /** JPEG or WebP compression from 0 to 100. */
    @SerialName("output_compression") public val outputCompression: Int? = null,
    /** Encoded image format. */
    @SerialName("output_format") public val outputFormat: ImageOutputFormat? = null,
    /** Number of partial images requested during streaming, from 0 to 3. */
    @SerialName("partial_images") public val partialImages: Int? = null,
    /** Stable, non-identifying end-user identifier. */
    @SerialName("safety_identifier") public val safetyIdentifier: String? = null,
)

/**
 * Image generation request.
 */
@BetaOpenAI
public fun imageGeneration(block: ImageGenerationRequestBuilder.() -> Unit): ImageGenerationRequest =
    ImageGenerationRequestBuilder().apply(block).build()

/**
 * Builder of [ImageGenerationRequest] instances.
 */
@BetaOpenAI
@OpenAIDsl
public class ImageGenerationRequestBuilder {

    /**
     * A text description of the desired image(s). GPT Image accepts up to 32000 characters.
     */
    public var prompt: String? = null

    /**
     * The number of images to generate. Must be between 1 and 10.
     */
    public var n: Int? = null

    /**
     * The size of the generated images.
     */
    public var size: ImageSize? = null

    /**
     * Deprecated end-user identifier; prefer safetyIdentifier.
     */
    public var user: String? = null

    /**
     * The image model. Supply an explicit current GPT Image model for generation.
     */
    public var model: ModelId? = null

    /** Background transparency. */
    public var background: ImageBackground? = null
    /** Image moderation level. */
    public var moderation: ImageModeration? = null
    /** JPEG or WebP compression from 0 to 100. */
    public var outputCompression: Int? = null
    /** Encoded image format. */
    public var outputFormat: ImageOutputFormat? = null
    /** Number of partial images requested during streaming, from 0 to 3. */
    public var partialImages: Int? = null
    /** Stable, non-identifying end-user identifier. */
    public var safetyIdentifier: String? = null
    /** Image quality. */
    public var quality: Quality? = null
    /** Legacy image style; unsupported by GPT Image. */
    public var style: Style? = null

    /** Creates an image generation request. */
    public fun build(): ImageGenerationRequest = ImageGenerationRequest(
        prompt = requireNotNull(prompt),
        n = n,
        size = size,
        user = user,
        model = requireNotNull(model) { "model is required" },
        quality = quality,
        style = style,
        background = background,
        moderation = moderation,
        outputCompression = outputCompression,
        outputFormat = outputFormat,
        partialImages = partialImages,
        safetyIdentifier = safetyIdentifier,
    )
}
