package com.aallam.openai.api.image
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline
/**
 * Control how much effort the model will exert to match the style and features,
 * especially facial features, of input images.
 * This parameter is only supported for gpt-image-1
 */
@Serializable
@JvmInline
public value class InputFidelity(public val value: String) {
    public companion object {

        public val High: InputFidelity = InputFidelity("high")

        public val Low: InputFidelity = InputFidelity("low")
    }
}
