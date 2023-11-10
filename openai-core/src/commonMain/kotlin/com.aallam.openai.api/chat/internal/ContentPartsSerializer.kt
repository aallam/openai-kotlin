package com.aallam.openai.api.chat.internal

import com.aallam.openai.api.chat.*
import kotlinx.serialization.KSerializer
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.listSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

internal class ContentPartsSerializer : KSerializer<ListContent> {
    override val descriptor: SerialDescriptor = listSerialDescriptor(ContentPart.serializer().descriptor)

    override fun deserialize(decoder: Decoder): ListContent {
        val parts = ListSerializer(ContentPart.serializer()).deserialize(decoder)
        return ListContent(parts)
    }

    override fun serialize(encoder: Encoder, value: ListContent) {
        ListSerializer(ContentPart.serializer()).serialize(encoder, value.content)
    }
}

internal class ContentPartSerializer : KSerializer<ContentPart> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ContentPart") {
        element("type", String.serializer().descriptor)
        element("text", String.serializer().descriptor, isOptional = true)
        element("image", String.serializer().descriptor, isOptional = true)
    }

    override fun deserialize(decoder: Decoder): ContentPart {
        require(decoder is JsonDecoder) { "This decoder is not a JsonDecoder. Cannot deserialize `Message`" }
        val json = decoder.decodeJsonElement() as JsonObject
        val type = json["type"]?.jsonPrimitive?.content
        val serializer = when (type) {
            "text" -> TextPart.serializer()
            "image" -> ImagePart.serializer()
            else -> throw UnsupportedOperationException("Cannot deserialize ContentPart. Unsupported type $type.")
        }
        return serializer.deserialize(decoder)
    }

    override fun serialize(encoder: Encoder, value: ContentPart) {
        when (value) {
            is ImagePart -> ImagePart.serializer().serialize(encoder, value)
            is TextPart -> TextPart.serializer().serialize(encoder, value)
        }
    }
}