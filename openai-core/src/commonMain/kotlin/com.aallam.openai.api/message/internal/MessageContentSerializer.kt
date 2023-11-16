package com.aallam.openai.api.message.internal

import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.MessageImageContent
import com.aallam.openai.api.message.MessageTextContent
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

internal class MessageContentSerializer : KSerializer<MessageContent> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("MessageContent")

    override fun deserialize(decoder: Decoder): MessageContent {
        require(decoder is JsonDecoder) { "This decoder is not a JsonDecoder. Cannot deserialize `MessageContent`" }
        val json = decoder.decodeJsonElement() as JsonObject
        return when (val type = json["type"]?.jsonPrimitive?.content) {
            "image_file" -> MessageImageContent.serializer().deserialize(decoder)
            "text" -> MessageTextContent.serializer().deserialize(decoder)
            else -> error("Unknown message content type $type")
        }
    }

    override fun serialize(encoder: Encoder, value: MessageContent) {
        when (value) {
            is MessageImageContent -> MessageImageContent.serializer().serialize(encoder, value)
            is MessageTextContent -> MessageTextContent.serializer().serialize(encoder, value)
        }
    }
}