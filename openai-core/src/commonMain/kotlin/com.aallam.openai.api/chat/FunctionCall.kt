package com.aallam.openai.api.chat

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The function to use for generating the completion.
 */
@JvmInline
@Serializable
@BetaOpenAI
public value class FunctionCall(public val functionCall: String) {
    public companion object {
        public val Auto: FunctionCall = FunctionCall("auto")
        public val None: FunctionCall = FunctionCall("none")
    }
}