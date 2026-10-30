package com.aallam.openai.api.image

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/** Fidelity to the input images on models supporting this control. Arbitrary future values are accepted. */
@Serializable
@JvmInline
public value class ImageInputFidelity(public val value: String) {
    public companion object {
        /** Selects low. */
        public val Low: ImageInputFidelity = ImageInputFidelity("low")
        /** Selects high. */
        public val High: ImageInputFidelity = ImageInputFidelity("high")
    }
}
