package com.aallam.openai.api.message

import com.aallam.openai.api.file.FileId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * References an Attachment in the message request.
 */
@Serializable
public data class Attachment(
    /**
     * The ID of the file to attach to the message.
     */
    @SerialName("file_id") val fileId: FileId
)
