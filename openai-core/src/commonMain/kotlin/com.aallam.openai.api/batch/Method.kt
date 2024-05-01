package com.aallam.openai.api.batch

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * HTTP Method.

 */
@Serializable
@JvmInline
public value class Method(public val value: String) {
    public companion object {
        public val Get: Method = Method("GET")
        public val Post: Method = Method("POST")
        public val Put: Method = Method("PUT")
        public val Delete: Method = Method("DELETE")
    }
}
