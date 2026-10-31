package com.aallam.openai.api.core

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
public value class Endpoint(public val path: String) {
    public companion object {
        public val Completions: Endpoint = Endpoint("/v1/chat/completions")

        /** Responses batch endpoint. */
        public val Responses: Endpoint = Endpoint("/v1/responses")

        /** Embeddings batch endpoint. */
        public val Embeddings: Endpoint = Endpoint("/v1/embeddings")
    }
}
