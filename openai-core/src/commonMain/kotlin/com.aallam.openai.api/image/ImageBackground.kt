package com.aallam.openai.api.image

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/** Background of a generated image. Arbitrary future values are accepted. */
@Serializable
@JvmInline
public value class ImageBackground(public val value: String) {
    public companion object {
        /** Selects auto. */
        public val Auto: ImageBackground = ImageBackground("auto")
        /** Selects opaque. */
        public val Opaque: ImageBackground = ImageBackground("opaque")
        /** Selects transparent. */
        public val Transparent: ImageBackground = ImageBackground("transparent")
    }
}
