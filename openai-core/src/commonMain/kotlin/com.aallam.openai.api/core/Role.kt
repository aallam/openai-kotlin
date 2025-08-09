package com.aallam.openai.api.core

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The role of the author of a message.
 */
@JvmInline
@Serializable
public value class Role(public val role: String) {
    public companion object {
        public val System: Role = Role("system")
        public val Developer: Role = Role("developer")
        public val User: Role = Role("user")
        public val Assistant: Role = Role("assistant")
        public val Function: Role = Role("function")
        public val Tool: Role = Role("tool")
    }
}
