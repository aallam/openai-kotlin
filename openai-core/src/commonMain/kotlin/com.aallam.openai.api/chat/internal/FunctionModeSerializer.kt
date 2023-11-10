package com.aallam.openai.api.chat.internal

import com.aallam.openai.api.chat.ToolChoice
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive

internal class ToolChoiceSerializer : KSerializer<ToolChoice> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ToolChoice")

    override fun deserialize(decoder: Decoder): ToolChoice {
        require(decoder is JsonDecoder) { "This decoder is not a JsonDecoder. Cannot deserialize `ToolChoice`" }
        return when (val json = decoder.decodeJsonElement()) {
            is JsonPrimitive -> ToolChoice.Mode(json.content)
            is JsonObject -> ToolChoice.Named.serializer().deserialize(decoder)
            else -> throw UnsupportedOperationException("Cannot deserialize ToolChoice. Unsupported JSON element.")
        }
    }

    override fun serialize(encoder: Encoder, value: ToolChoice) {
        require(encoder is JsonEncoder) { "This encoder is not a JsonEncoder. Cannot serialize `ToolChoice`" }
        when (value) {
            is ToolChoice.Mode -> encoder.encodeString(value.value)
            is ToolChoice.Named -> ToolChoice.Named.serializer().serialize(encoder, value)
        }
    }
}
