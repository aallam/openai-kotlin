package com.aallam.openai.api.chat.internal

import com.aallam.openai.api.chat.Content
import com.aallam.openai.api.chat.ListContent
import com.aallam.openai.api.chat.TextContent
import kotlinx.serialization.InternalSerializationApi
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.PolymorphicKind
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonPrimitive

internal class ContentSerializer : KSerializer<Content> {

    @OptIn(InternalSerializationApi::class)
    override val descriptor: SerialDescriptor = buildSerialDescriptor("Content", PolymorphicKind.SEALED)

    override fun deserialize(decoder: Decoder): Content {
        require(decoder is JsonDecoder) { "This decoder is not a JsonDecoder. Cannot deserialize `Content`" }
        val json = decoder.decodeJsonElement()
        val serializer = when (json) {
            is JsonPrimitive -> TextContent.serializer()
            is JsonArray -> ListContent.serializer()
            else -> throw UnsupportedOperationException("Cannot deserialize Content. Unsupported JSON element.")
        }
        return decoder.json.decodeFromJsonElement(serializer, json)
    }

    override fun serialize(encoder: Encoder, value: Content) {
        when (value) {
            is ListContent -> ListContent.serializer().serialize(encoder, value)
            is TextContent -> TextContent.serializer().serialize(encoder, value)
        }
    }
}
