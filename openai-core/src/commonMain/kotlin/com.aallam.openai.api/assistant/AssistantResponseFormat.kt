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
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonPrimitive

/**
 * string: auto is the default value
 *
 * object: An object describing the expected output of the model. If json_object only function type tools are allowed to be passed to the Run.
 * If text, the model can return text or any value needed.
 * type: string Must be one of text or json_object.
 */
@BetaOpenAI
@Serializable(with = AssistantResponseFormat.ResponseFormatSerializer::class)
public data class AssistantResponseFormat(
    val format: String? = null,
    val objectType: AssistantResponseType? = null,
) {
    @Serializable
    public data class AssistantResponseType(
        val type: String
    )

    public companion object {
        public val AUTO: AssistantResponseFormat = AssistantResponseFormat(format = "auto")
        public val TEXT: AssistantResponseFormat = AssistantResponseFormat(objectType = AssistantResponseType(type = "text"))
        public val JSON_OBJECT: AssistantResponseFormat = AssistantResponseFormat(objectType = AssistantResponseType(type = "json_object"))
    }

    public object ResponseFormatSerializer : KSerializer<AssistantResponseFormat> {
        override val descriptor: SerialDescriptor = buildClassSerialDescriptor("AssistantResponseFormat") {
            element<String>("format", isOptional = true)
            element<AssistantResponseType>("type", isOptional = true)
        }

        override fun serialize(encoder: Encoder, value: AssistantResponseFormat) {
            val jsonEncoder = encoder as? kotlinx.serialization.json.JsonEncoder
                ?: throw SerializationException("This class can be saved only by Json")

            if (value.format != null) {
                jsonEncoder.encodeJsonElement(JsonPrimitive(value.format))
            } else if (value.objectType != null) {
                val jsonElement: JsonElement = JsonObject(mapOf("type" to JsonPrimitive(value.objectType.type)))
                jsonEncoder.encodeJsonElement(jsonElement)
            }
        }

        override fun deserialize(decoder: Decoder): AssistantResponseFormat {
            val jsonDecoder = decoder as? kotlinx.serialization.json.JsonDecoder
                ?: throw SerializationException("This class can be loaded only by Json")

            val jsonElement = jsonDecoder.decodeJsonElement()
            return when {
                jsonElement is JsonPrimitive && jsonElement.isString -> {
                    AssistantResponseFormat(format = jsonElement.content)
                }
                jsonElement is JsonObject && "type" in jsonElement -> {
                    val type = jsonElement["type"]!!.jsonPrimitive.content
                    AssistantResponseFormat(objectType = AssistantResponseType(type))
                }
                else -> throw SerializationException("Unknown response format: $jsonElement")
            }
        }
    }
}
