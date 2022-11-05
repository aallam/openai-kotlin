package com.aallam.openai.api.image

public interface ImageEdit {
    /**
     * The image to edit. Must be a valid PNG file, less than 4MB, and square.
     */
    public val image: String

    /**
     * An additional image whose fully transparent areas (e.g. where alpha is zero) indicate where [image] should be
     * edited. Must be a valid PNG file, less than 4MB, and have the same dimensions as [image].
     */
    public val mask: String

    /**
     * A text description of the desired image(s). The maximum length is 1000 characters.
     */
    public val prompt: String

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

public data class ImageEditURL(
    override val image: String,
    override val mask: String,
    override val prompt: String,
    override val n: Int? = null,
    override val size: ImageSize? = null,
    override val user: String? = null,
) : ImageEdit

public data class ImageEditJSON(
    override val image: String,
    override val mask: String,
    override val prompt: String,
    override val n: Int? = null,
    override val size: ImageSize? = null,
    override val user: String? = null,
) : ImageEdit
