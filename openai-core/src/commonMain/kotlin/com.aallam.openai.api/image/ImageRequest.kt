package com.aallam.openai.api.image

import com.aallam.openai.api.ExperimentalOpenAI

/**
 * Image request.
 */
@ExperimentalOpenAI
public interface ImageRequest {

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
 * Image request with prompt.
 */
@ExperimentalOpenAI
public interface ImageRequestPrompt : ImageRequest {

    /**
     * A text description of the desired image(s). The maximum length is 1000 characters.
     */
    public val prompt: String
}
