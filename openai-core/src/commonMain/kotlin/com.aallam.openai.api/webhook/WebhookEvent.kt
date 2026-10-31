package com.aallam.openai.api.webhook

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/** A webhook event preserving all fields, including future event types and data. */
@Serializable(with = WebhookEvent.Serializer::class)
public data class WebhookEvent(
    /** Complete event object exactly as parsed from the verified JSON body. */
    public val payload: JsonObject,
) {
    init {
        require(payload["id"] is JsonPrimitive && payload.getValue("id").jsonPrimitive.isString) { "Event id must be a string" }
        require(payload["type"] is JsonPrimitive && payload.getValue("type").jsonPrimitive.isString) { "Event type must be a string" }
        require(payload["created_at"] is JsonPrimitive && !payload.getValue("created_at").jsonPrimitive.isString && payload.getValue("created_at").jsonPrimitive.longOrNull != null) { "Event created_at must be an integer" }
        require(payload["data"] is JsonObject) { "Event data must be an object" }
    }

    /** Unique event identifier; use it for application-level deduplication. */
    public val id: String get() = payload.getValue("id").jsonPrimitive.content
    /** Open-ended event discriminator, such as response.completed. */
    public val type: String get() = payload.getValue("type").jsonPrimitive.content
    /** Event creation time in Unix seconds, distinct from the delivery timestamp. */
    public val createdAt: Long get() = payload.getValue("created_at").jsonPrimitive.long
    /** Event-specific data, including fields not yet known by this client. */
    public val data: JsonObject get() = payload.getValue("data").jsonObject

    internal object Serializer : KSerializer<WebhookEvent> {
        override val descriptor: SerialDescriptor = JsonObject.serializer().descriptor
        override fun serialize(encoder: Encoder, value: WebhookEvent) {
            encoder.encodeSerializableValue(JsonObject.serializer(), value.payload)
        }
        override fun deserialize(decoder: Decoder): WebhookEvent = WebhookEvent(
            decoder.decodeSerializableValue(JsonObject.serializer())
        )
    }
}
