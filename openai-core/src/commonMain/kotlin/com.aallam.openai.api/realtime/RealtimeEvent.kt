package com.aallam.openai.api.realtime

import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/** A client or server protocol event. The full JSON payload retains unknown and tool-specific fields. */
@Serializable(with = RealtimeEvent.Serializer::class)
public data class RealtimeEvent(public val payload: JsonObject) {
    /** Protocol event discriminator. */
    public val type: String get() = payload.getValue("type").jsonPrimitive.content
    /** Event identifier, when supplied. */
    public val eventId: String? get() = payload["event_id"]?.jsonPrimitive?.contentOrNull
    /** Text, audio or function-argument delta, depending on type. Audio deltas are base64 encoded. */
    public val delta: String? get() = payload["delta"]?.jsonPrimitive?.contentOrNull
    /** Function call identifier. */
    public val callId: String? get() = payload["call_id"]?.jsonPrimitive?.contentOrNull
    /** Error payload on service error events. */
    public val error: JsonObject? get() = payload["error"] as? JsonObject
    init { require(payload["type"] is JsonPrimitive && payload["type"]!!.jsonPrimitive.isString) { "An event type is required" } }

    public companion object {
        /** Appends base64 audio to a conversation or translation session input buffer. */
        public fun appendAudio(audio: String): RealtimeEvent = event("input_audio_buffer.append") { put("audio", audio) }
        /** Commits a manually managed conversation audio buffer. */
        public fun commitAudio(): RealtimeEvent = event("input_audio_buffer.commit")
        /** Clears the conversation audio buffer. */
        public fun clearAudio(): RealtimeEvent = event("input_audio_buffer.clear")
        /** Updates session configuration; explicit JSON null can disable supported settings. */
        public fun updateSession(session: JsonObject): RealtimeEvent = event("session.update") { put("session", session) }
        /** Requests a model response. */
        public fun createResponse(response: JsonObject? = null): RealtimeEvent = event("response.create") { response?.let { put("response", it) } }
        /** Cancels an in-progress model response. */
        public fun cancelResponse(): RealtimeEvent = event("response.cancel")
        /** Supplies a function tool result as a conversation item. Request a response separately when needed. */
        public fun functionOutput(callId: String, output: String): RealtimeEvent = event("conversation.item.create") {
            putJsonObject("item") { put("type", "function_call_output"); put("call_id", callId); put("output", output) }
        }
        /** Closes a continuous translation session gracefully. */
        public fun closeTranslation(): RealtimeEvent = event("session.close")
        private fun event(type: String, body: JsonObjectBuilder.() -> Unit = {}): RealtimeEvent =
            RealtimeEvent(buildJsonObject { put("type", type); body() })
    }

    /** JSON serializer that preserves the event payload. */
    public object Serializer : KSerializer<RealtimeEvent> {
        override val descriptor: SerialDescriptor = JsonObject.serializer().descriptor
        override fun serialize(encoder: Encoder, value: RealtimeEvent) { encoder.encodeSerializableValue(JsonObject.serializer(), value.payload) }
        override fun deserialize(decoder: Decoder): RealtimeEvent = RealtimeEvent(decoder.decodeSerializableValue(JsonObject.serializer()))
    }
}
