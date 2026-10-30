package com.aallam.openai.api.image

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/** Encoding format of a generated image. Arbitrary future values are accepted. */
@Serializable
@JvmInline
public value class ImageOutputFormat(public val value: String) {
    public companion object {
        /** Selects png. */
        public val Png: ImageOutputFormat = ImageOutputFormat("png")
        /** Selects jpeg. */
        public val Jpeg: ImageOutputFormat = ImageOutputFormat("jpeg")
        /** Selects webp. */
        public val Webp: ImageOutputFormat = ImageOutputFormat("webp")
    }
}
