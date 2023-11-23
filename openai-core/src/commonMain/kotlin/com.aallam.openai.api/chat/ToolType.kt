package com.aallam.openai.api.chat

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The type of the tool.
 */
@JvmInline
@Serializable
public value class ToolType(public val value: String) {
    public companion object {
        /**
         * Represents 'function' tool.
         */
        public val Function: ToolType = ToolType("function")
    }
}
