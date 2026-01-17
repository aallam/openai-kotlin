package com.aallam.openai.api.image

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents an image event emitted during image creation or image editing streaming.
 */
@Serializable
public data class PartialImage(

    /**
     * Base64-encoded image data, suitable for rendering as an image.
     */
    @SerialName("b64_json")
    public val b64Json: String,

    /**
     * 0-based index for the partial image (streaming).
     * Only present if it's not the final image.
     */
    @SerialName("partial_image_index")
    public val partialImageIndex: Int? = null,

    /**
     * The type of the event.
     * For partial images: `image_generation.partial_image` or `image_edit.partial_image`.
     * For completed images: `image_generation.completed` or `image_edit.completed`.
     */
    @SerialName("type")
    private val type: String,

    /**
     * Whether this is the final image of the stream
     */
    public val isFinalImage: Boolean = type.endsWith("completed"),
)
