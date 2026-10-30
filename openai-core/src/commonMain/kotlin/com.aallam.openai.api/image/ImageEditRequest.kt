package com.aallam.openai.api.image

import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** A source image referenced by exactly one uploaded file ID or URL/data URL. */
@Serializable
public class ImageReference private constructor(
    @SerialName("file_id") public val fileId: FileId? = null,
    @SerialName("image_url") public val imageUrl: String? = null,
) {
    public companion object {
        /** References an uploaded image file. */
        public fun file(id: FileId): ImageReference = ImageReference(fileId = id)
        /** References an HTTPS image URL or base64 data URL. */
        public fun url(url: String): ImageReference = ImageReference(imageUrl = url)
    }
    init { require((fileId == null) != (imageUrl == null)) { "Provide exactly one image reference" } }
}

/** Edits one to sixteen referenced images using the current JSON image API. */
@Serializable
public data class ImageEditRequest(
    /** Source images, in reference order. */
    @SerialName("images") public val images: List<ImageReference>,
    /** Description of the desired edit. */
    @SerialName("prompt") public val prompt: String,
    /** Image model; omitted to use the service default. */
    @SerialName("model") public val model: ModelId? = null,
    /** Number of output images. */
    @SerialName("n") public val n: Int? = null,
    /** Output dimensions. */
    @SerialName("size") public val size: ImageSize? = null,
    /** Output quality. */
    @SerialName("quality") public val quality: Quality? = null,
    /** Fidelity control for supporting models. */
    @SerialName("input_fidelity") public val inputFidelity: ImageInputFidelity? = null,
    /** Optional edit mask; cannot be combined with markup. */
    @SerialName("mask") public val mask: ImageReference? = null,
    /** Optional markup overlay; cannot be combined with mask. */
    @SerialName("markup") public val markup: ImageReference? = null,
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
    /** Deprecated end-user identifier; prefer safetyIdentifier. */
    @SerialName("user") public val user: String? = null,
) {
    init {
        require(images.size in 1..16) { "Provide between one and sixteen images" }
        require(mask == null || markup == null) { "Mask and markup cannot be combined" }
    }
}
