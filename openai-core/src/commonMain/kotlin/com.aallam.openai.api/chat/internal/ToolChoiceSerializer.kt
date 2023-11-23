package com.aallam.openai.api.chat.internal

import com.aallam.openai.api.chat.ToolChoice
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

/**
 * Serializer for [ToolChoice].
 */
internal class ToolChoiceSerializer : JsonContentPolymorphicSerializer<ToolChoice>(ToolChoice::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ToolChoice> {
        return when (element) {
            is JsonPrimitive -> ToolChoice.Mode.serializer()
            is JsonObject -> ToolChoice.Named.serializer()
            else -> throw UnsupportedOperationException("Unsupported JSON element: $element")
        }
    }
}
