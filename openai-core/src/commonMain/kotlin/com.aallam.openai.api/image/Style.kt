package com.aallam.openai.api.image

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The style of the generated images.
 */
@Serializable
@JvmInline
public value class Style(public val value: String) {
    public companion object {
        public val Vivid: Style = Style("vivid")
        public val Natural: Style = Style("natural")
    }
}
