package com.aallam.openai.api.responses

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * Constrains the verbosity of the model's response.
 *
 * Lower values are more concise; higher values are more detailed.
 */
@JvmInline
@Serializable
public value class Verbosity(public val value: String) {
    public companion object {
        /** Concise responses. */
        public val Low: Verbosity = Verbosity("low")
        /** Balanced level of detail. */
        public val Medium: Verbosity = Verbosity("medium")
        /** Detailed, more verbose responses. */
        public val High: Verbosity = Verbosity("high")
    }
}
