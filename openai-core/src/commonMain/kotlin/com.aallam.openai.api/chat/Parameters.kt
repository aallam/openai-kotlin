package com.aallam.openai.api.chat

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/**
 * Represents parameters that a function accepts, described as a JSON Schema object.
 *
 * @property schema Json Schema object.
 */
@Serializable(with = Parameters.JsonDataSerializer::class)
public data class Parameters(public val schema: JsonElement) {

    /**
     * Custom serializer for the [Parameters] class.
     */
    public object JsonDataSerializer : KSerializer<Parameters> {
        override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

        /**
         * Deserializes [Parameters] from JSON format.
         */
        override fun deserialize(decoder: Decoder): Parameters {
            require(decoder is JsonDecoder) { "This decoder is not a JsonDecoder. Cannot deserialize `FunctionParameters`." }
            return Parameters(decoder.decodeJsonElement())
        }

        /**
         * Serializes [Parameters] to JSON format.
         */
        override fun serialize(encoder: Encoder, value: Parameters) {
            require(encoder is JsonEncoder) { "This encoder is not a JsonEncoder. Cannot serialize `FunctionParameters`." }
            encoder.encodeJsonElement(value.schema)
        }
    }

    public companion object {

        /**
         * Creates a [Parameters] instance from a JSON string.
         *
         * @param json The JSON string to parse.
         */
        public fun fromJsonString(json: String): Parameters = Parameters(Json.parseToJsonElement(json))

        /**
         * Creates a [Parameters] instance using a [JsonObjectBuilder].
         *
         * @param block The [JsonObjectBuilder] to use.
         */
        public fun buildJsonObject(block: JsonObjectBuilder.() -> Unit): Parameters {
            val json = kotlinx.serialization.json.buildJsonObject(block)
            return Parameters(json)
        }
    }
}
