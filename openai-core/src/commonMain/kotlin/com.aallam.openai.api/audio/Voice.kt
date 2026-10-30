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
        /** Ash built-in voice. */
        public val Ash: Voice = Voice("ash")
        /** Ballad built-in voice. */
        public val Ballad: Voice = Voice("ballad")
        /** Coral built-in voice. */
        public val Coral: Voice = Voice("coral")
        /** Sage built-in voice. */
        public val Sage: Voice = Voice("sage")
        /** Verse built-in voice. */
        public val Verse: Voice = Voice("verse")
        /** Marin built-in voice. */
        public val Marin: Voice = Voice("marin")
        /** Cedar built-in voice. */
        public val Cedar: Voice = Voice("cedar")

        public val Alloy: Voice = Voice("alloy")
        public val Echo: Voice = Voice("echo")
        public val Fable: Voice = Voice("fable")
        public val Onyx: Voice = Voice("onyx")
        public val Nova: Voice = Voice("nova")
        public val Shimmer: Voice = Voice("shimmer")
    }
}
