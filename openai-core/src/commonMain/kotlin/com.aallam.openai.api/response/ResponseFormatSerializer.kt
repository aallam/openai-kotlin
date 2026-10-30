package com.aallam.openai.api.response

import com.aallam.openai.api.chat.ChatResponseFormat
import com.aallam.openai.api.chat.JsonSchema
import kotlinx.serialization.KSerializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/** Adapts the shared format type to Responses' flat JSON schema representation. */
internal object ResponseFormatSerializer : KSerializer<ChatResponseFormat> {
    override val descriptor: SerialDescriptor = JsonObject.serializer().descriptor

    override fun serialize(encoder: Encoder, value: ChatResponseFormat) {
        require(encoder is JsonEncoder)
        val fields = mutableMapOf<String, JsonElement>("type" to JsonPrimitive(value.type))
        if (value.type == "json_schema") {
            val schema = requireNotNull(value.jsonSchema) { "JSON schema format requires a schema" }
            require(!schema.name.isNullOrBlank()) { "Responses JSON schema requires a name" }
            fields.putAll(encoder.json.encodeToJsonElement(JsonSchema.serializer(), schema).jsonObject)
        }
        encoder.encodeJsonElement(JsonObject(fields))
    }

    override fun deserialize(decoder: Decoder): ChatResponseFormat {
        require(decoder is JsonDecoder)
        val fields = decoder.decodeJsonElement().jsonObject
        val type = fields.getValue("type").jsonPrimitive.content
        val schema = if (type == "json_schema") {
            decoder.json.decodeFromJsonElement(JsonSchema.serializer(), JsonObject(fields - "type"))
        } else null
        return ChatResponseFormat(type, schema)
    }
}
