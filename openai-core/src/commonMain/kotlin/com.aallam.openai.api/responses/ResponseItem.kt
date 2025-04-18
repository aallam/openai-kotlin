package com.aallam.openai.api.responses

import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.*

@Serializable(with = ResponseItemSerializer::class)
public sealed interface ResponseItem

/**
 * Custom serializer for ResponseItem to handle disambiguation between input and output messages with the same serial name.
 */
internal class ResponseItemSerializer : JsonContentPolymorphicSerializer<ResponseItem>(ResponseItem::class) {
    private val json = Json { ignoreUnknownKeys = true }

    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ResponseItem> {
        return when (val type = element.jsonObject["type"]?.jsonPrimitive?.content) {
            "message" -> selectMessageDeserializer(element)
            //use default deserializer for other types
            else -> {
                json.serializersModule.getPolymorphic(ResponseItem::class, type)
                    ?: throw SerializationException("Unknown type: $type")
            }
        }
    }

    private fun selectMessageDeserializer(element: JsonElement): DeserializationStrategy<ResponseItem> {
        return when (element.jsonObject["role"]?.jsonPrimitive?.content) {
            "assistant" -> ResponseOutput.serializer()

            else -> json.serializersModule.getPolymorphic(ResponseInput::class, "message")
                ?: throw SerializationException("Unknown type: message")

        }
    }
}