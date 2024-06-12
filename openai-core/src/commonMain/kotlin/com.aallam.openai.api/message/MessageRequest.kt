package com.aallam.openai.api.message

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.file.FileId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Create a message request.
 */
@BetaOpenAI
@Serializable
public class MessageRequest(
    /**
     * The role of the entity that is creating the message. Currently only [Role.User] is supported.
     */
    @SerialName("role") public val role: Role,

    /**
     * The content of the message.
     */
    @SerialName("content") public val content: List<Content>,

    /**
     * A list of attachments that the message should use.
     */
    @SerialName("attachments") public val attachments: List<Attachment>? = null,

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    @SerialName("metadata") public val metadata: Map<String, String>? = null,
)

/**
 * A message request builder.
 */
@BetaOpenAI
public fun messageRequest(block: MessageRequestBuilder.() -> Unit): MessageRequest =
    MessageRequestBuilder().apply(block).build()

/**
 * A message request builder.
 */
@BetaOpenAI
public class MessageRequestBuilder {
    /**
     * The role of the entity that is creating the message. Currently only [Role.User] is supported.
     */
    public var role: Role? = null

    /**
     * The content of the message.
     */
    public var content: MutableList<Content> = mutableListOf()

    /**
     * A list of attachments that the message should use.
     */
    public var attachments: List<Attachment>? = null

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    public var metadata: Map<String, String>? = null

    public fun addTextContent(text: String) {
        content.add(Content.Text(text))
    }

    public fun addImageUrlContent(url: String, detail: String? = null) {
        content.add(Content.ImageUrl(url, detail))
    }

    public fun addImageFileContent(fileId: FileId) {
        content.add(Content.ImageFile(fileId))
    }

    public fun build(): MessageRequest = MessageRequest(
        role = requireNotNull(role) { "role is required" },
        content = content,
        attachments = attachments,
        metadata = metadata
    )
}

@BetaOpenAI
@Serializable
public sealed class Content {
    @Serializable
    @SerialName("text")
    public data class Text(val text: String) : Content()

    @Serializable
    @SerialName("image_url")
    public data class ImageUrl(val url: String, val detail: String? = null) : Content()

    @Serializable
    @SerialName("image_file")
    public data class ImageFile(val fileId: FileId) : Content()
}

@BetaOpenAI
@Serializable
public class Attachment(
    /**
     * The ID of the file to attach.
     */
    @SerialName("file_id") public val fileId: FileId,

    /**
     * A list of tools associated with this attachment.
     */
    @SerialName("tools") public val tools: List<Tool>? = null,
)

@BetaOpenAI
@Serializable
public class Tool(
    /**
     * The type of the tool.
     */
    @SerialName("type") public val type: String
)