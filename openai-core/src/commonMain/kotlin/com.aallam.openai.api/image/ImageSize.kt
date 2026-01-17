package com.aallam.openai.api.image

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The size of the generated images.
 *
 * - For GPT image models: [is1024x1024], [is1536x1024] (landscape), [is1024x1536] (portrait), or [Auto] (default)
 * - For DALL-E-3: [is1024x1024], [is1792x1024], or [is1024x1792]
 * - For DALL-E-2: [is256x256], [is512x512], or [is1024x1024]
 */
@JvmInline
@Serializable
public value class ImageSize(public val size: String) {

    public companion object {

        /**
         * Automatically select the best size for the given model (default for GPT models).
         */
        public val Auto: ImageSize = ImageSize("auto")

        /**
         * Size image of dimension 256x256 (DALL-E-2 only).
         */
        public val is256x256: ImageSize = ImageSize("256x256")

        /**
         * Size image of dimension 512x512 (DALL-E-2 only).
         */
        public val is512x512: ImageSize = ImageSize("512x512")

        /**
         * Size image of dimension 1024x1024 (all models).
         */
        public val is1024x1024: ImageSize = ImageSize("1024x1024")

        /**
         * Size image of dimension 1536x1024 landscape (GPT image models only).
         */
        public val is1536x1024: ImageSize = ImageSize("1536x1024")

        /**
         * Size image of dimension 1024x1536 portrait (GPT image models only).
         */
        public val is1024x1536: ImageSize = ImageSize("1024x1536")

        /**
         * Size image of dimension 1792x1024 landscape (DALL-E-3 only).
         */
        public val is1792x1024: ImageSize = ImageSize("1792x1024")

        /**
         * Size image of dimension 1024x1792 portrait (DALL-E-3 only).
         */
        public val is1024x1792: ImageSize = ImageSize("1024x1792")
    }
}
