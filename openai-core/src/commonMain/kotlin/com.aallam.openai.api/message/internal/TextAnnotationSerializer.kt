package com.aallam.openai.api.message.internal

import com.aallam.openai.api.message.FileCitationAnnotation
import com.aallam.openai.api.message.FilePathAnnotation
import com.aallam.openai.api.message.TextAnnotation
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

internal class TextAnnotationSerializer : KSerializer<TextAnnotation> {

    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("TextAnnotation")

    override fun deserialize(decoder: Decoder): TextAnnotation {
        require(decoder is JsonDecoder) { "This decoder is not a JsonDecoder. Cannot deserialize `MessageContent`" }
        val json = decoder.decodeJsonElement() as JsonObject
        return when (val type = json["type"]?.jsonPrimitive?.content) {
            "file_citation" -> FileCitationAnnotation.serializer().deserialize(decoder)
            "file_path" -> FilePathAnnotation.serializer().deserialize(decoder)
            else -> error("Unknown annotation content type $type")
        }
    }

    override fun serialize(encoder: Encoder, value: TextAnnotation) {
        when (value) {
            is FileCitationAnnotation -> FileCitationAnnotation.serializer().serialize(encoder, value)
            is FilePathAnnotation -> FilePathAnnotation.serializer().serialize(encoder, value)
        }
    }
}
