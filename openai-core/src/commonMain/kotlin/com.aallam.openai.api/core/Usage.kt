package com.aallam.openai.api.core

import kotlinx.serialization.*
import kotlinx.serialization.descriptors.*
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

@Serializable(with = UsageSerializer::class)
public data class Usage(
    /**
     * Count of prompts tokens.
     */
    public val promptTokens: Int? = null,
    /**
     * Count of completion tokens.
     * Also accepts "output_tokens" for compatibility with Responses API.
     */
    public val completionTokens: Int? = null,
    /**
     * Count of total tokens.
     */
    public val totalTokens: Int? = null,
)

/**
 * Custom serializer for Usage that handles both "completion_tokens" and "output_tokens" field names.
 */
internal object UsageSerializer : KSerializer<Usage> {
    override val descriptor: SerialDescriptor = buildClassSerialDescriptor("Usage") {
        element<Int?>("prompt_tokens")
        element<Int?>("completion_tokens")
        element<Int?>("total_tokens")
    }

    override fun serialize(encoder: Encoder, value: Usage) {
        val composite = encoder.beginStructure(descriptor)
        composite.encodeNullableSerializableElement(descriptor, 0, serializer<Int>(), value.promptTokens)
        composite.encodeNullableSerializableElement(descriptor, 1, serializer<Int>(), value.completionTokens)
        composite.encodeNullableSerializableElement(descriptor, 2, serializer<Int>(), value.totalTokens)
        composite.endStructure(descriptor)
    }

    override fun deserialize(decoder: Decoder): Usage {
        require(decoder is JsonDecoder) { "This serializer can only be used with Json format" }
        val element = decoder.decodeJsonElement().jsonObject

        val promptTokens = element["prompt_tokens"]?.jsonPrimitive?.intOrNull ?: element["input_tokens"]?.jsonPrimitive?.intOrNull
        val completionTokens = element["completion_tokens"]?.jsonPrimitive?.intOrNull ?: element["output_tokens"]?.jsonPrimitive?.intOrNull
        val totalTokens = element["total_tokens"]?.jsonPrimitive?.intOrNull

        return Usage(promptTokens, completionTokens, totalTokens)
    }
}
