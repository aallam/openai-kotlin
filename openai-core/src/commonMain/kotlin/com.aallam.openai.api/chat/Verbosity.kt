package com.aallam.openai.api.chat

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Verbosity level for controlling response length and detail.
 */
@Serializable
@JvmInline
public value class Verbosity(public val id: String) {
    public companion object {
        /**
         * Low verbosity - terse, concise responses.
         */
        public val Low: Verbosity = Verbosity("low")
        
        /**
         * Medium verbosity - balanced responses (default).
         */
        public val Medium: Verbosity = Verbosity("medium")
        
        /**
         * High verbosity - detailed, comprehensive responses.
         */
        public val High: Verbosity = Verbosity("high")
    }
}
