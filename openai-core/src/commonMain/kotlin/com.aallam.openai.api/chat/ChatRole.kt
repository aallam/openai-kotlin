package com.aallam.openai.api.chat

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The role of the author of this message.
 */
@JvmInline
@Serializable
public value class ChatRole(public val role: String) {
    public companion object {
        public val System: ChatRole = ChatRole("system")
        public val User: ChatRole = ChatRole("user")
        public val Assistant: ChatRole = ChatRole("assistant")
    }
}
