package com.aallam.openai.api.chat

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.decodeFromJsonElement
import kotlinx.serialization.json.encodeToJsonElement
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * An object specifying the format that the model must output.
 */
@Serializable(with = ChatResponseFormat.Serializer::class)
public data class ChatResponseFormat(
    /**
     * Response format type.
     */
    @SerialName("type") public val type: String,

    /**
     * Optional JSON schema specification when type is "json_schema"
     */
    @SerialName("json_schema") public val jsonSchema: JsonSchema? = null
) {
    public companion object {
        /**
         * JSON mode, which guarantees the message the model generates, is valid JSON.
         */
        public val JsonObject: ChatResponseFormat = ChatResponseFormat(type = "json_object")

        /**
         * Default text mode.
         */
        public val Text: ChatResponseFormat = ChatResponseFormat(type = "text")

        /**
         * Creates a JSON schema response format with the specified schema
         */
        public fun jsonSchema(schema: JsonSchema): ChatResponseFormat =
            ChatResponseFormat(type = "json_schema", jsonSchema = schema)
    }

    internal object Serializer : KSerializer<ChatResponseFormat> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ChatResponseFormat") {
            element<String>("type")
            element<JsonSchema>("json_schema", isOptional = true)
        }

        override fun deserialize(decoder: Decoder): ChatResponseFormat {
            require(decoder is JsonDecoder) { "This class can be loaded only by Json" }

            val element = decoder.decodeJsonElement()
            return when (element) {
                is JsonPrimitive -> ChatResponseFormat(type = element.content)
                is JsonObject -> {
                    val type = element["type"]?.jsonPrimitive?.content
                        ?: throw SerializationException("Missing response format type.")
                    val schema = element["json_schema"]?.jsonObject?.let {
                        decoder.json.decodeFromJsonElement<JsonSchema>(it)
                    }
                    ChatResponseFormat(type = type, jsonSchema = schema)
                }

                else -> throw SerializationException("Unknown response format: $element")
            }
        }

        override fun serialize(encoder: Encoder, value: ChatResponseFormat) {
            require(encoder is JsonEncoder) { "This class can be saved only by Json" }

            val element = when (value.type) {
                "json_schema" -> {
                    val content = mutableMapOf<String, JsonElement>("type" to JsonPrimitive("json_schema"))
                    value.jsonSchema?.let { content["json_schema"] = encoder.json.encodeToJsonElement(it) }
                    JsonObject(content)
                }

                else -> JsonObject(mapOf("type" to JsonPrimitive(value.type)))
            }
            encoder.encodeJsonElement(element)
        }
    }
}

/**
 * Specification for JSON schema response format
 */
@Serializable
public data class JsonSchema(
    /**
     * Optional name for the schema
     */
    @SerialName("name") val name: String? = null,

    /**
     * The JSON schema specification
     */
    @SerialName("schema") val schema: JsonObject,

    /**
     * Whether to enforce strict schema validation
     */
    @SerialName("strict") val strict: Boolean? = null
)
