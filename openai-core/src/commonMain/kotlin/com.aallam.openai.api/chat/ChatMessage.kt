package com.aallam.openai.api.chat

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * The messages to generate chat completions for.
 */
@Serializable
public class ChatMessage(
    /**
     * The role of the author of this message.
     */
    @SerialName("role") public val role: ChatRole,

    /**
     * The contents of the message.
     */
    @SerialName("content") public val content: String,

    /**
     * The name of the user in a multi-user chat.
     */
    @SerialName("name") public val name: String? = null
)

/**
 * The messages to generate chat completions for.
 */
public fun chatMessage(block: ChatMessageBuilder.() -> Unit): ChatMessage =
    ChatMessageBuilder().apply(block).build()

/**
 * Builder of [ChatMessageBuilder] instances.
 */
public class ChatMessageBuilder {

    /**
     * The role of the author of this message.
     */
    public var role: ChatRole? = null

    /**
     * The contents of the message.
     */
    public var content: String? = null

    /**
     * The name of the user in a multi-user chat.
     */
    public var name: String? = null

    /**
     * Create [ChatMessageBuilder] instance.
     */
    public fun build(): ChatMessage = ChatMessage(
        role = requireNotNull(role) { "role is required" },
        content = requireNotNull(content) { "content is required" },
        name = name,
    )
}
