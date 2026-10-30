package com.aallam.openai.api.image

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/** Moderation level for generated images. Arbitrary future values are accepted. */
@Serializable
@JvmInline
public value class ImageModeration(public val value: String) {
    public companion object {
        /** Selects auto. */
        public val Auto: ImageModeration = ImageModeration("auto")
        /** Selects low. */
        public val Low: ImageModeration = ImageModeration("low")
    }
}
