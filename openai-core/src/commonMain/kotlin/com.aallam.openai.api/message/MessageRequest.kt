package com.aallam.openai.api.message

import com.aallam.openai.api.core.Role
import com.aallam.openai.api.file.FileId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Create a message request.
 */
@Serializable
public class MessageRequest(
    /**
     * The role of the entity that is creating the message. Currently only [Role.User] is supported.
     */
    @SerialName("role") public val role: Role,

    /**
     * The content of the message.
     */
    @SerialName("content") public val content: String,

    /**
     * A list of File IDs that the message should use.
     * There can be a maximum of 10 files attached to a message.
     * Useful for tools like retrieval and code interpreter that can access and use files.
     */
    @SerialName("file_ids") public val fileIds: List<FileId>? = null,

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
public fun messageRequest(block: MessageRequestBuilder.() -> Unit): MessageRequest =
    MessageRequestBuilder().apply(block).build()

/**
 * A message request builder.
 */
public class MessageRequestBuilder {
    /**
     * The role of the entity that is creating the message. Currently only [Role.User] is supported.
     */
    public var role: Role? = null

    /**
     * The content of the message.
     */
    public var content: String? = null

    /**
     * A list of File IDs that the message should use.
     * There can be a maximum of 10 files attached to a message.
     * Useful for tools like retrieval and code interpreter that can access and use files.
     */
    public var fileIds: List<FileId>? = null

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    public var metadata: Map<String, String>? = null

    public fun build(): MessageRequest = MessageRequest(
        role = requireNotNull(role) { "role is required" },
        content = requireNotNull(content) { "content is required" },
        fileIds = fileIds,
        metadata = metadata
    )
}
