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
    public suspend fun imageURL(creation: ImageCreation): List<ImageURL>

    /**
     * Creates an image given a prompt.
     * Get images as base 64 JSON.
     */
    @ExperimentalOpenAI
    public suspend fun imageJSON(creation: ImageCreation): List<ImageJSON>

    /**
     * Creates an edited or extended image given an original image and a prompt.
     * Get images as URLs.
     */
    @ExperimentalOpenAI
    public suspend fun imageURL(edit: ImageEdit): List<ImageURL>

    /**
     * Creates an edited or extended image given an original image and a prompt.
     * Get images as base 64 JSON.
     */
    @ExperimentalOpenAI
    public suspend fun imageJSON(edit: ImageEdit): List<ImageJSON>

    /**
     * Creates a variation of a given image.
     * Get images as URLs.
     */
    @ExperimentalOpenAI
    public suspend fun imageURL(variation: ImageVariation): List<ImageURL>

    /**
     * Creates a variation of a given image.
     * Get images as base 64 JSON.
     */
    @ExperimentalOpenAI
    public suspend fun imageJSON(variation: ImageVariation): List<ImageJSON>
}
