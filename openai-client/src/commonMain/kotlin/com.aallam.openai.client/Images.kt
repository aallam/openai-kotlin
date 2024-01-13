package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.image.*

/**
 * Given a prompt and/or an input image, the model will generate a new image.
 */
public interface Images {

    /**
     * Creates an image given a prompt.
     * Get images as URLs.
     *
     * @param creation image creation request.
     * @param requestOptions request options.
     */
    public suspend fun imageURL(creation: ImageCreation, requestOptions: RequestOptions? = null): List<ImageURL>

    /**
     * Creates an image given a prompt.
     * Get images as base 64 JSON.
     *
     * @param creation image creation request.
     * @param requestOptions request options.
     */
    public suspend fun imageJSON(creation: ImageCreation, requestOptions: RequestOptions? = null): List<ImageJSON>

    /**
     * Creates an edited or extended image given an original image and a prompt.
     * Get images as URLs.
     *
     * @param edit image edit request.
     * @param requestOptions request options.
     */
    public suspend fun imageURL(edit: ImageEdit, requestOptions: RequestOptions? = null): List<ImageURL>

    /**
     * Creates an edited or extended image given an original image and a prompt.
     * Get images as base 64 JSON.
     *
     * @param edit image edit request.
     * @param requestOptions request options.
     */
    public suspend fun imageJSON(edit: ImageEdit, requestOptions: RequestOptions? = null): List<ImageJSON>

    /**
     * Creates a variation of a given image.
     * Get images as URLs.
     *
     * @param variation image variation request.
     * @param requestOptions request options.
     */
    public suspend fun imageURL(variation: ImageVariation, requestOptions: RequestOptions? = null): List<ImageURL>

    /**
     * Creates a variation of a given image.
     * Get images as base 64 JSON.
     *
     * @param variation image variation request.
     * @param requestOptions request options.
     */
    public suspend fun imageJSON(variation: ImageVariation, requestOptions: RequestOptions? = null): List<ImageJSON>
}
