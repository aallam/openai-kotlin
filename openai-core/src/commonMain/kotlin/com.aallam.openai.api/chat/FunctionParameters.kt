package com.aallam.openai.api.chat

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*
import kotlin.jvm.JvmInline

/**
 * Represents parameters that a function accepts, described as a JSON Schema object.
 *
 * @property schema Json Schema object.
 */
@JvmInline
@Serializable(with = FunctionParameters.JsonDataSerializer::class)
public value class FunctionParameters(public val schema: JsonElement) {

    /**
     * Custom serializer for the [FunctionParameters] class.
     */
    public object JsonDataSerializer : KSerializer<FunctionParameters> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("FunctionParameters")

        /**
         * Deserializes [FunctionParameters] from JSON format.
         */
        override fun deserialize(decoder: Decoder): FunctionParameters {
            require(decoder is JsonDecoder) { "This decoder is not a JsonDecoder. Cannot deserialize `FunctionParameters`." }
            return FunctionParameters(decoder.decodeJsonElement())
        }

        /**
         * Serializes [FunctionParameters] to JSON format.
         */
        override fun serialize(encoder: Encoder, value: FunctionParameters) {
            require(encoder is JsonEncoder) { "This encoder is not a JsonEncoder. Cannot serialize `FunctionParameters`." }
            encoder.encodeJsonElement(value.schema)
        }
    }

    public companion object {

        /**
         * Creates a [FunctionParameters] instance from a JSON string.
         *
         * @param json The JSON string to parse.
         */
        public fun fromJsonString(json: String): FunctionParameters = FunctionParameters(Json.parseToJsonElement(json))

        /**
         * Creates a [FunctionParameters] instance using a [JsonObjectBuilder].
         *
         * @param block The [JsonObjectBuilder] to use.
         */
        public fun builder(block: JsonObjectBuilder.() -> Unit): FunctionParameters {
            val json = buildJsonObject(block)
            return FunctionParameters(json)
        }
    }
}
