package com.aallam.openai.api.core

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
public value class FinishReason(public val value: String) {
    public companion object {
        public val Stop: FinishReason = FinishReason("stop")
        public val Length: FinishReason = FinishReason("length")
        public val FunctionCall: FinishReason = FinishReason("function_call")
    }
}