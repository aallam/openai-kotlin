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
    @SerialName("content") public val content: String,

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
    public var content: String? = null

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    public var metadata: Map<String, String>? = null

    public fun build(): MessageRequest = MessageRequest(
        role = requireNotNull(role) { "role is required" },
        content = requireNotNull(content) { "content is required" },
        metadata = metadata
    )
}
