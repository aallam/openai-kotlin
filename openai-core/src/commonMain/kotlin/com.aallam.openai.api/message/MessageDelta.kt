package com.aallam.openai.api.message

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.file.FileId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Represents a message delta i.e., any changed fields on a message during streaming.
 */
@BetaOpenAI
@Serializable
public data class MessageDelta(
    /**
     * The identifier of the message, which can be referenced in API endpoints.
     */
    @SerialName("id") val id: MessageId,
    /**
     * The delta containing the fields that have changed on the Message.
     */
    @SerialName("delta") val content: String,
)

/**
 * The delta containing the fields that have changed on the Message.
 */
@BetaOpenAI
@Serializable
public data class MessageDeltaRole(
    /**
     * The entity that produced the message. One of [Role.User] or [Role.Assistant].
     */
    @SerialName("role") val role: Role,
    /**
     * The content of the message in an array of text and/or images.
     */
    @SerialName("content") val content: List<DeltaContent>,
    /**
     * A list of file IDs that the assistant should use. Useful for tools like retrieval and code_interpreter that can
     * access files. A maximum of 10 files can be attached to a message.
     */
    @SerialName("file_ids") val fileIds: List<FileId>,
)

/**
 * The content of the message in an array of text and/or images.
 */
@BetaOpenAI
@Serializable
public sealed interface DeltaContent

/**
 * References an image File in the content of a message.
 */
@BetaOpenAI
@Serializable
@SerialName("image_file")
public data class DeltaImageFile(
    /**
     * The index of the content part in the message.
     */
    @SerialName("index") val index: Long,
    /**
     * The File ID of the image in the message content.
     */
    @SerialName("image_file") val imageFile: ImageFile,
) : DeltaContent

/**
 * The text content that is part of a message.
 */
@BetaOpenAI
@Serializable
@SerialName("text")
public data class DeltaText(
    /**
     * The index of the content part in the message.
     */
    @SerialName("index") val index: Long,
    /**
     * The text content of the message value and annotations.
     */
    @SerialName("text") val text: TextContent,
) : DeltaContent
