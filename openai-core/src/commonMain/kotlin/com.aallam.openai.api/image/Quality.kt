package com.aallam.openai.api.image

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The quality of the image that will be generated.
 *
 * - For GPT image models: supports [Auto], [High], [Medium], [Low]
 * - For DALL-E-3: supports [HD], [Standard]
 * - For DALL-E-2: only [Standard] is supported
 */
@Serializable
@JvmInline
public value class Quality(public val value: String) {
    public companion object {
        /**
         * Automatically select the best quality for the given model (default for GPT models).
         */
        public val Auto: Quality = Quality("auto")

        /**
         * High quality (GPT image models only).
         */
        public val High: Quality = Quality("high")

        /**
         * Medium quality (GPT image models only).
         */
        public val Medium: Quality = Quality("medium")

        /**
         * Low quality (GPT image models only).
         */
        public val Low: Quality = Quality("low")

        /**
         * HD quality (DALL-E-3 only).
         */
        public val HD: Quality = Quality("hd")

        /**
         * Standard quality (DALL-E-2 and DALL-E-3).
         */
        public val Standard: Quality = Quality("standard")
    }
}
