package com.aallam.openai.api.chat

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Generated chat message.
 */
@Serializable
@BetaOpenAI
public data class ChatDelta internal constructor(
    /**
     * The role of the author of this message.
     */
    @SerialName("role") val role: ChatRole? = null,

    /**
     * The contents of the message.
     */
    @SerialName("content") val content: String? = null,

    /**
     * The name of the user in a multi-user chat.
     */
    @SerialName("name") public val name: String? = null
)
