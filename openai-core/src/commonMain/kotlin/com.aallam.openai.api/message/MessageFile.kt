package com.aallam.openai.api.message

import com.aallam.openai.api.file.FileId
import kotlinx.serialization.Serializable

/**
 * File attached to a message.
 */
@Serializable
public data class MessageFile(
    /**
     * The identifier, which can be referenced in API endpoints.
     */
    val id: FileId,
    /**
     * The Unix timestamp (in seconds) for when the message file was created.
     */
    val createdAt: Int,
    /**
     * The ID of the message that the File is attached to.
     */
    val messageId: MessageId,
)
