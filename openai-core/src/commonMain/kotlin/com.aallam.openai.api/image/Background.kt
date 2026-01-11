package com.aallam.openai.api.image
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
/**
 * Allows setting transparency for the background of the generated image(s).
 * This parameter is only supported for the GPT image models.
 */
@Serializable
@JvmInline
public value class Background(public val value: String) {
    public companion object {
        /**
         * The model will automatically determine the best background for the image (default).
         */
        public val Auto: Background = Background("auto")
        /**
         * Transparent background. The output format needs to support transparency (png or webp).
         */
        public val Transparent: Background = Background("transparent")
        /**
         * Opaque background.
         */
        public val Opaque: Background = Background("opaque")
    }
}
