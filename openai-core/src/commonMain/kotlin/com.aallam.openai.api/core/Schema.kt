package com.aallam.openai.api.core

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.put
import kotlinx.serialization.json.putJsonObject

/**
 * The schema for the response format, described as a JSON Schema object.
 *
 * @property schema Json Schema Object.
 */
@Serializable(with = Schema.JsonDataSerializer::class)
public data class Schema(public val schema: JsonElement) {

    /**
     * Custom serializer for the [Schema] class.
     */
    public object JsonDataSerializer : KSerializer<Schema> {
        override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

        /**
         * Deserializes [Schema] from JSON format.
         */
        override fun deserialize(decoder: Decoder): Schema {
            require(decoder is JsonDecoder) { "This decoder is not a JsonDecoder. Cannot deserialize `JsonSchema`." }
            return Schema(decoder.decodeJsonElement())
        }

        /**
         * Serializes [Schema] to JSON format.
         */
        override fun serialize(encoder: Encoder, value: Schema) {
            require(encoder is JsonEncoder) { "This encoder is not a JsonEncoder. Cannot serialize `JsonSchema`." }
            encoder.encodeJsonElement(value.schema)
        }
    }

    public companion object {

        /**
         * Creates a [Schema] instance from a JSON string.
         *
         * @param json The JSON string to parse.
         */
        public fun fromJsonString(json: String): Schema = Schema(Json.parseToJsonElement(json))

        /**
         * Creates a [Schema] instance using a [JsonObjectBuilder].
         *
         * @param block The [JsonObjectBuilder] to use.
         */
        public fun buildJsonObject(block: JsonObjectBuilder.() -> Unit): Schema {
            val json = kotlinx.serialization.json.buildJsonObject(block)
            return Schema(json)
        }

        /**
         * Represents a no params json. Equivalent to:
         * ```json
         * {"type": "object", "properties": {}}
         * ```
         */
        public val Empty: Schema = buildJsonObject {
            put("type", "object")
            putJsonObject("properties") {}
        }
    }
}
