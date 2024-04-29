package com.aallam.openai.api.core

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
public value class Endpoint(public val path: String) {
    public companion object {
        public val Completions: Endpoint = Endpoint("/v1/chat/completions")
    }
}
