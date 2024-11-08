package com.aallam.openai.api.core

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

/**
 * Represents the format of the response.
 * Response format can be of types: auto, text, json_object, or json_schema.
 */
@BetaOpenAI
@Serializable(with = ResponseFormat.Serializer::class)
public sealed interface ResponseFormat {

    /**
     * The type of response format: text
     */
    @BetaOpenAI
    @Serializable
    @SerialName("auto")
    public data object AutoResponseFormat : ResponseFormat

    /**
     * The type of response format: text
     */
    @BetaOpenAI
    @Serializable
    @SerialName("text")
    public data object TextResponseFormat : ResponseFormat

    /**
     * The type of response format: json_object
     */
    @BetaOpenAI
    @Serializable
    @SerialName("json_object")
    public data object JsonObjectResponseFormat : ResponseFormat

    /**
     * The type of response format: json_schema
     */
    @BetaOpenAI
    @Serializable
    @SerialName("json_schema")
    public data class JsonSchemaResponseFormat(
        /**
         * The actual JSON schema.
         */
        @SerialName("json_schema") public val schema: JsonSchema,
    ) : ResponseFormat

    public object Serializer : KSerializer<ResponseFormat> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("ResponseFormat") {
            element<String>("type")
        }

        override fun serialize(encoder: Encoder, value: ResponseFormat) {
            require(encoder is JsonEncoder)
            val json = when (value) {
                is AutoResponseFormat -> JsonPrimitive("auto")
                is TextResponseFormat -> JsonObject(mapOf("type" to JsonPrimitive("text")))
                is JsonObjectResponseFormat -> JsonObject(mapOf("type" to JsonPrimitive("json_object")))
                is JsonSchemaResponseFormat -> JsonObject(mapOf("type" to JsonPrimitive("json_schema"), "json_schema" to Json.encodeToJsonElement(JsonSchema.serializer(), value.schema)))
            }
            encoder.encodeJsonElement(json)
        }

        override fun deserialize(decoder: Decoder): ResponseFormat {
            require(decoder is JsonDecoder)
            val json = decoder.decodeJsonElement()
            return when {
                json is JsonPrimitive && json.content == "auto" -> AutoResponseFormat
                json is JsonObject && json["type"]?.jsonPrimitive?.content == "text" -> TextResponseFormat
                json is JsonObject && json["type"]?.jsonPrimitive?.content == "json_object" -> JsonObjectResponseFormat
                json is JsonObject && json["type"]?.jsonPrimitive?.content == "json_schema" -> JsonSchemaResponseFormat(Json.decodeFromJsonElement(JsonSchema.serializer(), json["json_schema"]!!))
                else -> throw IllegalArgumentException("Unknown ResponseFormat: $json")
            }
        }
    }

}