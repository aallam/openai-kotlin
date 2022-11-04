package com.aallam.openai.client

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.image.ImageRequest
import com.aallam.openai.api.image.ImageURL

/**
 * Given a prompt and/or an input image, the model will generate a new image.
 */
public interface Images {

    /**
     * Creates an image given a prompt.
     */
    @ExperimentalOpenAI
    public suspend fun images(request: ImageRequest): List<ImageURL>
}
