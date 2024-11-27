package com.aallam.openai.api.message

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.message.MessageRequestContent.ListContent
import com.aallam.openai.api.message.MessageRequestContent.TextContent
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
    @SerialName("content") public val messageContent: MessageRequestContent,

    /**
     * A list of files attached to the message.
     */
    @SerialName("attachments") public val attachments: List<Attachment>? = null,

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    @SerialName("metadata") public val metadata: Map<String, String>? = null,
) {
    public constructor(
        role: Role,
        content: String,
        attachments: List<Attachment>? = null,
        metadata: Map<String, String>? = null,
    ) : this(
        role = role,
        messageContent = TextContent(content),
        attachments = attachments,
        metadata = metadata
    )

    public constructor(
        role: Role,
        content: List<MessageRequestPart>,
        attachments: List<Attachment>? = null,
        metadata: Map<String, String>? = null,
    ) : this(
        role = role,
        messageContent = ListContent(content),
        attachments = attachments,
        metadata = metadata
    )

    public val content: String
        get() = when (messageContent) {
            is TextContent -> messageContent.content
            else -> error("Content is not text")
        }
}

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
     * A list of files attached to the message.
     */
    public var attachments: List<Attachment>? = null

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    public var metadata: Map<String, String>? = null

    /**
     * The contents of the assistant request message.
     */
    internal val parts = mutableListOf<MessageRequestPart>()

    /**
     * The contents of the message.
     */
    public fun content(block: MessageRequestPartBuilder.() -> Unit) {
        this.parts += MessageRequestPartBuilder().apply(block).build()
    }

    /**
     * Create [MessageRequest] instance.
     */
    public fun build(): MessageRequest {
        require(!(content != null && parts.isNotEmpty())) { "Cannot set both content string and content parts" }
        val messageContent = content?.let { MessageRequestContent.TextContent(it) }
            ?: ListContent(parts)
        return MessageRequest(
            role = requireNotNull(role) { "role is required " },
            messageContent = messageContent,
            attachments = attachments,
            metadata = metadata
        )
    }
}

