package com.aallam.openai.api.chat

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The role of the author of this message.
 */
@JvmInline
@Serializable
@BetaOpenAI
public value class ChatRole(public val role: String) {
    public companion object {
        public val System: ChatRole = ChatRole("system")
        public val User: ChatRole = ChatRole("user")
        public val Assistant: ChatRole = ChatRole("assistant")
        public val Function: ChatRole = ChatRole("function")
    }
}
