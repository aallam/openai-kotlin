package com.aallam.openai.api.chat.internal

import com.aallam.openai.api.chat.Content
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
        val serializer = when (decoder.decodeJsonElement()) {
            is JsonPrimitive -> Content.Text.serializer()
            is JsonArray -> Content.Parts.serializer()
            else -> throw UnsupportedOperationException("Cannot deserialize Content. Unsupported JSON element.")
        }
        return serializer.deserialize(decoder)
    }

    override fun serialize(encoder: Encoder, value: Content) {
        when (value) {
            is Content.Parts -> Content.Parts.serializer().serialize(encoder, value)
            is Content.Text -> Content.Text.serializer().serialize(encoder, value)
        }
    }
}
