package com.aallam.openai.api.message

import com.aallam.openai.api.file.FileId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * References an image File in the content of a message.
 */
@Serializable
public data class ImageFile(
    /**
     * The File ID of the image in the message content.
     */
    @SerialName("file_id") val fileId: FileId,
    /**
     * Optional Defaults to auto
     * Specifies the detail level of the image if specified by the user. low uses fewer tokens, you can opt in to high resolution using high.
     */
    @SerialName("detail") val detail: String? = null,
)

/**
 * Image content part data.
 */
@Serializable
public data class ImageURL(
    /**
     * Either a URL of the image or the base64 encoded image data.
     */
    @SerialName("url") val url: String,

    /**
     * Specifies the detail level of the image.
     */
    @SerialName("detail") val detail: String? = null,
)