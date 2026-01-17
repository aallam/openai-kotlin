package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.image.*
import kotlinx.coroutines.flow.Flow

/**
 * Given a prompt and/or an input image, the model will generate a new image.
 */
public interface Images {

    /**
     * Creates an image given a prompt.
     * Returns images as base64 JSON (the only format supported by GPT image models).
     *
     * @param creation image creation request.
     * @param requestOptions request options.
     */
    public suspend fun imageCreate(creation: ImageCreation, requestOptions: RequestOptions? = null): List<ImageJSON>

    /**
     * Creates an edited or extended image given an original image and a prompt.
     * Returns images as base64 JSON (the only format supported by GPT image models).
     *
     * @param edit image edit request.
     * @param requestOptions request options.
     */
    public suspend fun imageEdit(edit: ImageEdit, requestOptions: RequestOptions? = null): List<ImageJSON>

    /**
     * Creates an image given a prompt and streams the response back as a series of partial images.
     * This allows you to stream partial images as they are generated, providing a more interactive experience.
     *
     * @param creation image creation request.
     * @param requestOptions request options.
     */
    public fun imageCreateFlow(creation: ImageCreation, requestOptions: RequestOptions? = null): Flow<PartialImage>

    /**
     * Creates an edited or extended image given an original image and a prompt
     * and streams the response back as a series of partial images.
     * This allows you to stream partial images as they are generated, providing a more interactive experience.
     *
     * @param edit image edit request.
     * @param requestOptions request options.
     */
    public fun imageEditFlow(edit: ImageEdit, requestOptions: RequestOptions? = null): Flow<PartialImage>

    /**
     * Creates an image given a prompt.
     * Get images as URLs.
     *
     * @param creation image creation request.
     * @param requestOptions request options.
     */
    @Deprecated(
        message = "Use image() for GPT models (returns base64). URL responses only work with DALL-E models which are being removed in May 2026.",
        replaceWith = ReplaceWith("image(creation, requestOptions)"),
        level = DeprecationLevel.WARNING
    )
    public suspend fun imageURL(creation: ImageCreation, requestOptions: RequestOptions? = null): List<ImageURL>

    /**
     * Creates an image given a prompt.
     * Get images as base 64 JSON.
     *
     * @param creation image creation request.
     * @param requestOptions request options.
     */
    @Deprecated(
        message = "Use image() instead for a unified API that works with both GPT and DALL-E models.",
        replaceWith = ReplaceWith("image(creation, requestOptions)"),
        level = DeprecationLevel.WARNING
    )
    public suspend fun imageJSON(creation: ImageCreation, requestOptions: RequestOptions? = null): List<ImageJSON>

    /**
     * Creates an edited or extended image given an original image and a prompt.
     * Get images as URLs.
     *
     * @param edit image edit request.
     * @param requestOptions request options.
     */
    @Deprecated(
        message = "Use image() for GPT models (returns base64). URL responses only work with DALL-E models which are being removed in May 2026.",
        replaceWith = ReplaceWith("image(edit, requestOptions)"),
        level = DeprecationLevel.WARNING
    )
    public suspend fun imageURL(edit: ImageEdit, requestOptions: RequestOptions? = null): List<ImageURL>

    /**
     * Creates an edited or extended image given an original image and a prompt.
     * Get images as base 64 JSON.
     *
     * @param edit image edit request.
     * @param requestOptions request options.
     */
    @Deprecated(
        message = "Use image() instead for a unified API that works with both GPT and DALL-E models.",
        replaceWith = ReplaceWith("image(edit, requestOptions)"),
        level = DeprecationLevel.WARNING
    )
    public suspend fun imageJSON(edit: ImageEdit, requestOptions: RequestOptions? = null): List<ImageJSON>

    /**
     * Creates a variation of a given image.
     * Get images as URLs.
     *
     * @param variation image variation request.
     * @param requestOptions request options.
     */
    @Deprecated(
        message = "Use image() for GPT models (returns base64). URL responses only work with DALL-E models which are being removed in May 2026.",
        replaceWith = ReplaceWith("image(variation, requestOptions)"),
        level = DeprecationLevel.WARNING
    )
    public suspend fun imageURL(variation: ImageVariation, requestOptions: RequestOptions? = null): List<ImageURL>

    /**
     * Creates a variation of a given image.
     * Get images as base 64 JSON.
     *
     * @param variation image variation request.
     * @param requestOptions request options.
     */
    @Deprecated(
        message = "Use image() instead for a unified API that works with both GPT and DALL-E models.",
        replaceWith = ReplaceWith("image(variation, requestOptions)"),
        level = DeprecationLevel.WARNING
    )
    public suspend fun imageJSON(variation: ImageVariation, requestOptions: RequestOptions? = null): List<ImageJSON>
}
