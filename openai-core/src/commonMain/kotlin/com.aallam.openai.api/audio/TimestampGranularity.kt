package com.aallam.openai.api.audio

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
public value class TimestampGranularity(public val value: String) {
    public companion object {
        public val Word: TimestampGranularity = TimestampGranularity("word")
        public val Segment: TimestampGranularity = TimestampGranularity("segment")
    }
}
