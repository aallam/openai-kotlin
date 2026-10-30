package com.aallam.openai.api.file

import com.aallam.openai.api.OpenAIDsl

/**
 * Request to upload a file.
 *
 * [documentation](https://developers.openai.com/api/reference/resources/files/methods/create)
 */
public class FileUpload(

    /**
     * The [JSON Lines](https://jsonlines.readthedocs.io/en/latest/) file to be uploaded.
     *
     * If the [purpose] is set to "fine-tune", each line is a JSON record with "prompt" and "completion" fields
     * representing your [training examples](https://beta.openai.com/docs/guides/fine-tuning/prepare-training-data).
     */
    public val file: FileSource,

    /**
     * The intended purpose of the uploaded documents.
     *
     * Use "fine-tune" for [Fine-tuning](https://developers.openai.com/api/reference/resources/fine_tuning/subresources/jobs/methods/create).
     * This allows us to validate the format of the uploaded file.
     */
    public val purpose: Purpose,

    /** Optional expiration policy for the uploaded file. */
    public val expiresAfter: FileExpiration? = null,
)

/**
 * Request to upload a file.
 *
 * [documentation](https://developers.openai.com/api/reference/resources/files/methods/create)
 */
public fun fileUpload(block: FileUploadBuilder.() -> Unit): FileUpload = FileUploadBuilder().apply(block).build()

/**
 * Builders of [FileUpload] instances.
 */
@OpenAIDsl
public class FileUploadBuilder {

    /**
     * The [JSON Lines](https://jsonlines.readthedocs.io/en/latest/) file to be uploaded.
     *
     * If the [purpose] is set to "fine-tune", each line is a JSON record with "prompt" and "completion" fields
     * representing your [training examples](https://beta.openai.com/docs/guides/fine-tuning/prepare-training-data).
     */
    public var file: FileSource? = null

    /**
     * The intended purpose of the uploaded documents.
     *
     * Use "fine-tune" for [Fine-tuning](https://developers.openai.com/api/reference/resources/fine_tuning/subresources/jobs/methods/create).
     * This allows us to validate the format of the uploaded file.
     */
    public var purpose: Purpose? = null

    /** Optional expiration policy for the uploaded file. */
    public var expiresAfter: FileExpiration? = null

    /**
     * Create a new instance of [FileUpload].
     */
    public fun build(): FileUpload = FileUpload(
        file = requireNotNull(file) { "file is required" },
        purpose = requireNotNull(purpose) { "purpose is required" },
        expiresAfter = expiresAfter,
    )
}
