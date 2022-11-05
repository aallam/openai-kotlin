package com.aallam.openai.client

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.image.*

/**
 * Given a prompt and/or an input image, the model will generate a new image.
 */
public interface Images {

    /**
     * Creates an image given a prompt.
     * Get images as URLs.
     */
    @ExperimentalOpenAI
    public suspend fun images(request: ImageRequestURL): List<ImageURL>

    /**
     * Creates an image given a prompt.
     * Get images as base 64 JSON.
     */
    @ExperimentalOpenAI
    public suspend fun images(request: ImageRequestJSON): List<ImageJSON>

    /**
     * Creates an edited or extended image given an original image and a prompt.
     * Get images as URLs.
     */
    @ExperimentalOpenAI
    public suspend fun image(edit: ImageEditURL): List<ImageURL>

    /**
     * Creates an edited or extended image given an original image and a prompt.
     * Get images as base 64 JSON.
     */
    @ExperimentalOpenAI
    public suspend fun image(edit: ImageEditJSON): List<ImageJSON>
}
