package com.aallam.openai.api.batch

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The batch identifier.
 */
@BetaOpenAI
@JvmInline
@Serializable
public value class CompletionWindow(public val value: String) {
    public companion object {
        public val TwentyFourHours: CompletionWindow = CompletionWindow("24h")
    }
}
