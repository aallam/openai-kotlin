package com.aallam.openai.api.audio

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
public value class AudioResponseFormat(public val value: String) {
    public companion object {
        public val Json: AudioResponseFormat = AudioResponseFormat("json")
        public val Text: AudioResponseFormat = AudioResponseFormat("text")
        public val Srt: AudioResponseFormat = AudioResponseFormat("srt")
        public val VerboseJson: AudioResponseFormat = AudioResponseFormat("verbose_json")
        public val Vtt: AudioResponseFormat = AudioResponseFormat("vtt")
    }
}
