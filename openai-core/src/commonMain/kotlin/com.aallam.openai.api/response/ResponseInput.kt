package com.aallam.openai.api.response

import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.encodeToJsonElement

/**
 * Input for a response request.
 *
 * This wraps either a plain string input or structured input items.
 */
@Serializable(with = ResponseInput.Serializer::class)
public data class ResponseInput(public val value: JsonElement) {

    /**
     * Creates a plain text response input.
     */
    public constructor(text: String) : this(value = JsonPrimitive(text))

    /**
     * Creates structured response input from a list of input items.
     */
    public constructor(items: List<ResponseInputItem>) : this(value = Json.encodeToJsonElement(items))

    public object Serializer : KSerializer<ResponseInput> {
        override val descriptor: SerialDescriptor = JsonElement.serializer().descriptor

        override fun deserialize(decoder: Decoder): ResponseInput {
            require(decoder is JsonDecoder) { "This class can be loaded only by Json" }
            return ResponseInput(decoder.decodeJsonElement())
        }

        override fun serialize(encoder: Encoder, value: ResponseInput) {
            require(encoder is JsonEncoder) { "This class can be saved only by Json" }
            encoder.encodeJsonElement(value.value)
        }
    }
}

/**
 * Structured response input item.
 */
@Serializable
public data class ResponseInputItem(
    @SerialName("id") public val id: String? = null,
    @SerialName("type") public val type: String? = null,
    @SerialName("status") public val status: String? = null,
    @SerialName("role") public val role: String? = null,
    @SerialName("content") public val content: JsonElement? = null,
)
