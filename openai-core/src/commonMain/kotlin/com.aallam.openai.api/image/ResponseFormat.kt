package com.aallam.openai.api.image

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The format in which the generated images are returned.
 */
@JvmInline
@Serializable
public value class ResponseFormat(public val format: String) {

    public companion object {

        /**
         * Response format as url.
         */
        public val url: ResponseFormat = ResponseFormat("url")

        /**
         * Response format as base 64 json.
         */
        public val base64Json: ResponseFormat = ResponseFormat("b64_json")
    }
}
