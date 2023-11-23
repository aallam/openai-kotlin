package com.aallam.openai.api.audio

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The voice to use when generating the audio
 */
@Serializable
@JvmInline
public value class Voice(public val value: String) {
    public companion object {
        public val Alloy: Voice = Voice("alloy")
        public val Echo: Voice = Voice("echo")
        public val Fable: Voice = Voice("fable")
        public val Onyx: Voice = Voice("onyx")
        public val Nova: Voice = Voice("nova")
        public val Shimmer: Voice = Voice("shimmer")
    }
}