package com.aallam.openai.api.assistant

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.booleanOrNull
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Represents the format of the response from the assistant.
 *
 * @property type The type of the response format.
 * @property jsonSchema The JSON schema associated with the response format, if type is "json_schema" otherwise null.
 */
@BetaOpenAI
@Serializable(with = AssistantResponseFormat.ResponseFormatSerializer::class)
public data class AssistantResponseFormat(
    val type: String,
    val jsonSchema: JsonSchema? = null
) {

    /**
     * Represents a JSON schema.
     *
     * @property name The name of the schema.
     * @property description The description of the schema.
     * @property schema The actual JSON schema.
     * @property strict Indicates if the schema is strict.
     */
    @Serializable
    public data class JsonSchema(
        val name: String,
        val description: String? = null,
        val schema: JsonObject,
        val strict: Boolean? = null
    )

    public companion object {
        public val AUTO: AssistantResponseFormat = AssistantResponseFormat("auto")
        public val TEXT: AssistantResponseFormat = AssistantResponseFormat("text")
        public val JSON_OBJECT: AssistantResponseFormat = AssistantResponseFormat("json_object")

        /**
         * Creates an instance of `AssistantResponseFormat` with type `json_schema`.
         *
         * @param name The name of the schema.
         * @param description The description of the schema.
         * @param schema The actual JSON schema.
         * @param strict Indicates if the schema is strict.
         * @return An instance of `AssistantResponseFormat` with the specified JSON schema.
         */
        public fun JSON_SCHEMA(
            name: String,
            description: String? = null,
            schema: JsonObject,
            strict: Boolean? = null
        ): AssistantResponseFormat = AssistantResponseFormat(
            "json_schema",
            JsonSchema(name, description, schema, strict)
        )
    }


    public object ResponseFormatSerializer : KSerializer<AssistantResponseFormat> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AssistantResponseFormat") {
            element<String>("type")
            element<JsonSchema>("json_schema", isOptional = true)  // Only for "json_schema" type
        }

        override fun deserialize(decoder: Decoder): AssistantResponseFormat {
            val jsonDecoder = decoder as? kotlinx.serialization.json.JsonDecoder
                ?: throw SerializationException("This class can be loaded only by Json")

            val jsonElement = jsonDecoder.decodeJsonElement()
            return when {
                jsonElement is JsonPrimitive && jsonElement.isString -> {
                    AssistantResponseFormat(type = jsonElement.content)
                }
                jsonElement is JsonObject && "type" in jsonElement -> {
                    val type = jsonElement["type"]!!.jsonPrimitive.content
                    when (type) {
                        "json_schema" -> {
                            val schemaObject = jsonElement["json_schema"]?.jsonObject
                            val name = schemaObject?.get("name")?.jsonPrimitive?.content ?: ""
                            val description = schemaObject?.get("description")?.jsonPrimitive?.contentOrNull
                            val schema = schemaObject?.get("schema")?.jsonObject ?: JsonObject(emptyMap())
                            val strict = schemaObject?.get("strict")?.jsonPrimitive?.booleanOrNull
                            AssistantResponseFormat(
                                type = "json_schema",
                                jsonSchema = JsonSchema(name = name, description = description, schema = schema, strict = strict)
                            )
                        }
                        "json_object" -> AssistantResponseFormat(type = "json_object")
                        "auto" -> AssistantResponseFormat(type = "auto")
                        "text" -> AssistantResponseFormat(type = "text")
                        else -> throw SerializationException("Unknown response format type: $type")
                    }
                }
                else -> throw SerializationException("Unknown response format: $jsonElement")
            }
        }

        override fun serialize(encoder: Encoder, value: AssistantResponseFormat) {
            val jsonEncoder = encoder as? kotlinx.serialization.json.JsonEncoder
                ?: throw SerializationException("This class can be saved only by Json")

            val jsonElement = when (value.type) {
                "json_schema" -> {
                    JsonObject(
                        mapOf(
                            "type" to JsonPrimitive("json_schema"),
                            "json_schema" to JsonObject(
                                mapOf(
                                    "name" to JsonPrimitive(value.jsonSchema?.name ?: ""),
                                    "description" to JsonPrimitive(value.jsonSchema?.description ?: ""),
                                    "schema" to (value.jsonSchema?.schema ?: JsonObject(emptyMap())),
                                    "strict" to JsonPrimitive(value.jsonSchema?.strict ?: false)
                                )
                            )
                        )
                    )
                }
                "json_object" -> JsonObject(mapOf("type" to JsonPrimitive("json_object")))
                "auto" -> JsonPrimitive("auto")
                "text" -> JsonObject(mapOf("type" to JsonPrimitive("text")))
                else -> throw SerializationException("Unsupported response format type: ${value.type}")
            }
            jsonEncoder.encodeJsonElement(jsonElement)
        }

    }
}

public fun JsonObject.Companion.buildJsonObject(block: JsonObjectBuilder.() -> Unit): JsonObject {
    return kotlinx.serialization.json.buildJsonObject(block)
}
