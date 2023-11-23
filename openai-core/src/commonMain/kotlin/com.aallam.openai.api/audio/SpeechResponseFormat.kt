package com.aallam.openai.api.audio

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
public value class SpeechResponseFormat(public val value: String) {
    public companion object {
        public val Mp3: SpeechResponseFormat = SpeechResponseFormat("mp3")
        public val Opus: SpeechResponseFormat = SpeechResponseFormat("opus")
        public val Aac: SpeechResponseFormat = SpeechResponseFormat("aac")
        public val Flac: SpeechResponseFormat = SpeechResponseFormat("flac")
    }
}
