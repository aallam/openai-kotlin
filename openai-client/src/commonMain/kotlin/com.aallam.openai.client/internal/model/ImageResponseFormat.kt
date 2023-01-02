package com.aallam.openai.client.internal.model

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The format in which the generated images are returned.
 */
@JvmInline
@Serializable
internal value class ImageResponseFormat(val format: String) {

    companion object {

        /**
         * Response format as url.
         */
        val url: ImageResponseFormat = ImageResponseFormat("url")

        /**
         * Response format as base 64 json.
         */
        val base64Json: ImageResponseFormat = ImageResponseFormat("b64_json")
    }
}
