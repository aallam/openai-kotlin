package com.aallam.openai.api.thread

import com.aallam.openai.api.core.Role
import com.aallam.openai.api.file.FileId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A message in a thread.
 */
@Serializable
public data class ThreadMessage(
    /**
     * The role of the entity that is creating the message.
     * Currently only [Role.User] is supported.
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
     * Keys can be a maximum of 64 characters long, and values can be a maxium of 512 characters long.
     */
    @SerialName("metadata") public val metadata: Map<String, String>? = null,
)

public class ThreadMessageBuilder {

    /**
     * The role of the entity that is creating the message.
     * Currently only [Role.User] is supported.
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
     * Set of 16 key-value pairs that can be attached to a message.
     * This can be useful for storing additional information about the message in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    public var metadata: Map<String, String>? = null

    /**
     * Builds and returns a [ThreadMessage] instance.
     */
    public fun build(): ThreadMessage = ThreadMessage(
        role = requireNotNull(role) { "role must not be null" },
        content = requireNotNull(content) { "content must not be null" },
        fileIds = fileIds,
        metadata = metadata
    )
}

/**
 * Creates a [ThreadMessage] instance using the provided builder block.
 */
public fun threadMessage(block: ThreadMessageBuilder.() -> Unit): ThreadMessage =
    ThreadMessageBuilder().apply(block).build()