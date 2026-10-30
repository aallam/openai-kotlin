package com.aallam.openai.api.response

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/** A Responses stream event, retaining the complete payload for newly introduced event kinds. */
@Serializable(with = ResponseStreamEvent.Serializer::class)
public data class ResponseStreamEvent(
    /** Unmodified event payload, including tool-specific and error fields. */
    public val data: JsonObject,
) {
    /** Event discriminator, such as response.output_text.delta or response.completed. */
    public val type: String get() = data.getValue("type").jsonPrimitive.content
    /** Multiplexed conversation lane, absent for the implicit default lane. */
    public val streamId: String? get() = data["stream_id"]?.jsonPrimitive?.contentOrNull
    /** Sequence number used to resume a background response stream. */
    public val sequenceNumber: Int? get() = data["sequence_number"]?.jsonPrimitive?.intOrNull
    /** Text or function-argument delta, when supplied by this event. */
    public val delta: String? get() = (data["delta"] as? JsonPrimitive)?.contentOrNull
    /** Full response supplied by lifecycle events. */
    public val response: Response? get() = data["response"]?.let { eventJson.decodeFromJsonElement(Response.serializer(), it) }
    /** Output item supplied by output-item events. */
    public val item: ResponseOutputItem? get() = data["item"]?.let { eventJson.decodeFromJsonElement(ResponseOutputItem.serializer(), it) }

    internal object Serializer : KSerializer<ResponseStreamEvent> {
        override val descriptor: SerialDescriptor = JsonObject.serializer().descriptor
        override fun deserialize(decoder: Decoder): ResponseStreamEvent {
            require(decoder is JsonDecoder)
            val data = decoder.decodeJsonElement() as? JsonObject
                ?: throw SerializationException("Response stream events must be JSON objects")
            val type = data["type"] as? JsonPrimitive
            if (type == null || !type.isString) throw SerializationException("Response stream events require a string type")
            return ResponseStreamEvent(data)
        }
        override fun serialize(encoder: Encoder, value: ResponseStreamEvent) {
            require(encoder is JsonEncoder)
            encoder.encodeJsonElement(value.data)
        }
    }
}

private val eventJson = Json { ignoreUnknownKeys = true }
