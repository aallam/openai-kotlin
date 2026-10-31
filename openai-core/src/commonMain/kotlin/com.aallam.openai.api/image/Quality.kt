package com.aallam.openai.api.image

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The quality of the image that will be generated
 */
@Serializable
@JvmInline
public value class Quality(public val value: String) {
    public companion object {
        /** Selects auto. */
        public val Auto: Quality = Quality("auto")
        /** Selects low. */
        public val Low: Quality = Quality("low")
        /** Selects medium. */
        public val Medium: Quality = Quality("medium")
        /** Selects high. */
        public val High: Quality = Quality("high")
        /** Selects xhigh. */
        public val Xhigh: Quality = Quality("xhigh")
        /** Selects max. */
        public val Max: Quality = Quality("max")

        public val HD: Quality = Quality("hd")
    }
}
