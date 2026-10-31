package com.aallam.openai.api.chat.internal

import com.aallam.openai.api.chat.ChatAnnotation
import kotlinx.serialization.*
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.*
import kotlinx.serialization.json.*

/** Normalize nested Chat URL citations while preserving the legacy flat model. */
internal object ChatAnnotationSerializer : KSerializer<ChatAnnotation> {
    override val descriptor: SerialDescriptor = AnnotationFields.serializer().descriptor

    override fun deserialize(decoder: Decoder): ChatAnnotation {
        val fields = if (decoder is JsonDecoder) {
            val incoming = decoder.decodeJsonElement().jsonObject
            val citation = incoming["url_citation"] as? JsonObject
            val normalized = if (citation == null) incoming else JsonObject(
                incoming.filterKeys { it != "url_citation" } + citation.filterKeys {
                    it in setOf("url", "title", "start_index", "end_index")
                }
            )
            decoder.json.decodeFromJsonElement(AnnotationFields.serializer(), normalized)
        } else {
            decoder.decodeSerializableValue(AnnotationFields.serializer())
        }
        return ChatAnnotation(fields.type, fields.url, fields.title, fields.startIndex, fields.endIndex, fields.text)
    }

    override fun serialize(encoder: Encoder, value: ChatAnnotation) {
        encoder.encodeSerializableValue(
            AnnotationFields.serializer(),
            AnnotationFields(value.type, value.url, value.title, value.startIndex, value.endIndex, value.text)
        )
    }
}

@Serializable
@SerialName("com.aallam.openai.api.chat.ChatAnnotation")
private data class AnnotationFields(
    val type: String,
    val url: String? = null,
    val title: String? = null,
    @SerialName("start_index") val startIndex: Int? = null,
    @SerialName("end_index") val endIndex: Int? = null,
    val text: String? = null,
)
