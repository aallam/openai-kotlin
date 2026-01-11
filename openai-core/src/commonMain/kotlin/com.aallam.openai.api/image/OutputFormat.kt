package com.aallam.openai.api.image
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
/**
 * The format in which the generated images are returned.
 * This parameter is only supported for the GPT image models.
 */
@Serializable
@JvmInline
public value class OutputFormat(public val value: String) {
    public companion object {
        /**
         * PNG format (default).
         */
        public val PNG: OutputFormat = OutputFormat("png")
        /**
         * JPEG format.
         */
        public val JPEG: OutputFormat = OutputFormat("jpeg")
        /**
         * WebP format.
         */
        public val WebP: OutputFormat = OutputFormat("webp")
    }
}
