package com.aallam.openai.api.image

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The size of the generated images.
 */
@JvmInline
@Serializable
public value class ImageSize(public val size: String) {

    public companion object {

        /**
         * Size image of dimension 256x256.
         */
        public val is256x256: ImageSize = ImageSize("256x256")

        /**
         * Size image of dimension 512x512.
         */
        public val is512x512: ImageSize = ImageSize("512x512")

        /**
         * Size image of dimension 1024x1024.
         */
        public val is1024x1024: ImageSize = ImageSize("1024x1024")
    }
}
