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
    @SerialName("file_id") val fileId: FileId
)
