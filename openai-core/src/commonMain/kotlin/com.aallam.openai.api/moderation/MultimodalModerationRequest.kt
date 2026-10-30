package com.aallam.openai.api.moderation

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Classifies text and image content together using an omni-moderation model. */
@Serializable
public data class MultimodalModerationRequest(
    /** Text and image content to classify. */
    @SerialName("input") public val input: List<ModerationInput>,
    /** Model to use, or null to use the server default. */
    @SerialName("model") public val model: ModerationModel? = null,
)

/** A text or image content part in a multimodal moderation request. */
@Serializable
public sealed interface ModerationInput {
    /** Text to classify. */
    @Serializable
    @SerialName("text")
    public data class Text(
        /** Text content. */
        @SerialName("text") public val text: String,
    ) : ModerationInput

    /** Image to classify, supplied as an HTTPS URL or a base64 data URL. */
    @Serializable
    @SerialName("image_url")
    public data class Image(
        /** Image URL reference. */
        @SerialName("image_url") public val imageUrl: ImageUrl,
    ) : ModerationInput {
        /** Creates an image input from its URL or base64 data URL. */
        public constructor(url: String) : this(ImageUrl(url))
    }

    /** URL wrapper required by the image moderation wire format. */
    @Serializable
    public data class ImageUrl(
        /** HTTPS image URL or base64 data URL. */
        @SerialName("url") public val url: String,
    )
}
