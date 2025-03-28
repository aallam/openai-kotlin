package com.aallam.openai.api.responses

import com.aallam.openai.api.chat.ChatMessage
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*

/**
 * Text, image, or file inputs to the model, used to generate a response.
 * 
 * Can be either a simple text string or a list of messages.
 */
@Serializable(with = InputSerializer::class)
public sealed interface Input

/**
 * A text input to the model, equivalent to a text input with the `user` role.
 */
@Serializable
public data class TextInput(val text: String) : Input

/**
 * A list of one or many chat messages as input to the model.
 */
@Serializable
public data class MessageInput(val messages: List<ChatMessage>) : Input

internal class InputSerializer : JsonContentPolymorphicSerializer<Input>(Input::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<Input> {
        return when (element) {
            is JsonPrimitive -> TextInput.serializer()
            is JsonArray -> {
                // Check if this is a message array
                if (element.isNotEmpty() && element[0] is JsonObject &&
                    (element[0] as JsonObject).containsKey("role")
                ) {
                    MessageInput.serializer()
                } else {
                    // Plain strings array is no longer supported - throw error
                    throw SerializationException("Unsupported array input format")
                }
            }

            else -> throw SerializationException("Unsupported JSON element: $element")
        }
    }
}