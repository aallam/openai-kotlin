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

