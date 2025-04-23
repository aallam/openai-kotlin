package com.aallam.openai.api.responses

import com.aallam.openai.api.responses.ResponseInput.ListInput
import com.aallam.openai.api.responses.ResponseInput.TextInput
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlin.jvm.JvmInline

/**
 * Text, image, or file inputs to the model, used to generate a response.
 *
 * Can be either a simple text string or a list of messages.
 */
@Serializable(with = InputSerializer::class)
public sealed interface ResponseInput {
    /**
     * A text input to the model, equivalent to a text input with the `user` role.
     */
    @Serializable
    @JvmInline
    public value class TextInput(public val value: String) : ResponseInput

    /**
     * A list of chat messages as input to the model.
     */
    @Serializable
    @JvmInline
    public value class ListInput(public val values: List<ResponseItem>) : ResponseInput

    public companion object {
        /**
         * Create a text input from a string.
         */
        public fun from(text: String): ResponseInput = TextInput(text)

        /**
         * Create an input list from a list of items.
         */
        public fun from(items: List<ResponseItem>): ResponseInput = ListInput(items)
    }
}

/**
 * Custom serializer for Input that handles direct string or array serialization.
 */
internal class InputSerializer : JsonContentPolymorphicSerializer<ResponseInput>(ResponseInput::class) {

    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ResponseInput> {
        return when (element) {
            is JsonPrimitive -> TextInput.serializer()
            is JsonArray -> ListInput.serializer()
            else -> throw SerializationException("Unsupported JSON element: $element")
        }
    }
}
