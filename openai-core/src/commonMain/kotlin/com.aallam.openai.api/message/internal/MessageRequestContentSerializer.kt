package com.aallam.openai.api.message.internal


import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.message.MessageRequestContent
import com.aallam.openai.api.message.MessageRequestContent.ListContent
import com.aallam.openai.api.message.MessageRequestContent.TextContent
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive

/**
 * Serializer for [MessageRequestContent].
 */
@OptIn(BetaOpenAI::class)
internal class MessageRequestContentSerializer :
    JsonContentPolymorphicSerializer<MessageRequestContent>(MessageRequestContent::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<MessageRequestContent> {
        return when (element) {
            is JsonPrimitive -> TextContent.serializer()
            is JsonArray -> ListContent.serializer()
            else -> throw SerializationException("Unsupported JSON element: $element")
        }
    }
}
