package com.aallam.openai.api.message

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.file.FileId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * File attached to a message.
 */
@Deprecated("For beta assistant-v1 API only")
@BetaOpenAI
@Serializable
public data class MessageFile(
    /**
     * The identifier, which can be referenced in API endpoints.
     */
    @SerialName("id") val id: FileId,
    /**
     * The Unix timestamp (in seconds) for when the message file was created.
     */
    @SerialName("created_at") val createdAt: Int? = null,
    /**
     * The ID of the message that the File is attached to.
     */
    @SerialName("message_id") val messageId: MessageId? = null,
)
