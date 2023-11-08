package com.aallam.openai.api.chat

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

@Serializable(with = ChatResponseFormatSerializer::class)
public enum class ChatResponseFormat(
    internal val value: String,
) {
    JSON("json_object"),
    TEXT("text"),
}

internal object ChatResponseFormatSerializer : KSerializer<ChatResponseFormat> {
    private const val TYPE_PARAM = "type"

    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor(
        serialName = "ChatResponseFormat",
        PrimitiveKind.STRING,
    )

    override fun serialize(encoder: Encoder, value: ChatResponseFormat) {
        val json = JsonObject(mapOf(TYPE_PARAM to JsonPrimitive(value.value)))
        encoder.encodeSerializableValue(JsonElement.serializer(), json)
    }

    override fun deserialize(decoder: Decoder): ChatResponseFormat =
        (decoder.decodeSerializableValue(JsonElement.serializer()) as? JsonObject)?.let { json ->
            json[TYPE_PARAM]?.jsonPrimitive?.contentOrNull?.let { type ->
                when (type) {
                    ChatResponseFormat.JSON.value -> ChatResponseFormat.JSON
                    ChatResponseFormat.TEXT.value -> ChatResponseFormat.TEXT
                    else -> throw SerializationException("Unknown ChatResponseFormat type: $type")
                }
            }
        } ?: throw SerializationException("ChatResponseFormat deserialization failed")
}
