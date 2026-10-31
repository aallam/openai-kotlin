package com.aallam.openai.api.live

import com.aallam.openai.api.decision.Decision
import com.aallam.openai.api.decision.DecisionInputItem
import com.aallam.openai.api.response.ResponseInputEntry
import kotlinx.serialization.KSerializer
import kotlinx.serialization.Serializable
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.*

/** A Live protocol event retaining all original, including evolving backend-specific, JSON fields. */
@Serializable(with = LiveEvent.Serializer::class)
public data class LiveEvent(public val payload: JsonObject) {
    /** Event discriminator. */
    public val type: String get() = payload.getValue("type").jsonPrimitive.content
    /** Event identifier, when present. */
    public val eventId: String? get() = payload["event_id"]?.jsonPrimitive?.contentOrNull
    /** Correlated client command identifier, when present. */
    public val clientEventId: String? get() = payload["client_event_id"]?.jsonPrimitive?.contentOrNull
    /** Base64 audio or transcript text, according to [type]. */
    public val delta: String? get() = payload["delta"]?.jsonPrimitive?.contentOrNull
    /** Complete session snapshot, including fields unknown to this SDK version. */
    public val session: JsonObject? get() = payload["session"] as? JsonObject
    /** Opaque session identifier on lifecycle events. */
    public val sessionId: String? get() = session?.get("id")?.jsonPrimitive?.contentOrNull
    /** Delegation identifier used to correlate backend work. */
    public val delegationId: String? get() = payload["delegation_id"]?.jsonPrimitive?.contentOrNull
    /** Nested Responses or Decisions event, preserved without narrowing its variants. */
    public val event: JsonObject? get() = payload["event"] as? JsonObject
    /** Native Decisions result, when this is a decision.event response. */
    public val decision: Decision? get() = event?.get("decision")?.let { liveEventJson.decodeFromJsonElement<Decision>(it) }
    /** Cumulative Live audio seconds. Do not sum this value across events. */
    public val usage: LiveUsage? get() = payload["usage"]?.let { liveEventJson.decodeFromJsonElement<LiveUsage>(it) }
    /** Service error details. Errors remain events and do not implicitly terminate a connection. */
    public val error: LiveError? get() = payload["error"]?.let { liveEventJson.decodeFromJsonElement<LiveError>(it) }
    /** Session-relative audio/transcript start in milliseconds, when supplied. */
    public val startMs: Long? get() = payload["start_ms"]?.jsonPrimitive?.longOrNull
    /** Session-relative exclusive end in milliseconds, when supplied. */
    public val endMs: Long? get() = payload["end_ms"]?.jsonPrimitive?.longOrNull
    /** Latest context-window utilization, when supplied. */
    public val contextUsageRatio: Double? get() = (payload["context_window"] as? JsonObject)?.get("usage_ratio")?.jsonPrimitive?.doubleOrNull

    init { require(payload["type"] is JsonPrimitive && payload.getValue("type").jsonPrimitive.isString) { "A Live event type is required" } }

    public companion object {
        /** Starts a primary WebSocket. SDK-scoped primary connections send this automatically. */
        public fun start(session: LiveSessionConfig, eventId: String? = null): LiveEvent =
            event("session.start", eventId) { put("session", liveEventJson.encodeToJsonElement(session)) }
        /** Starts a stored-session fork. The source model is inherited. */
        public fun startFork(session: LiveSessionOverrides = LiveSessionOverrides(), eventId: String? = null): LiveEvent =
            event("session.start", eventId) { put("session", liveEventJson.encodeToJsonElement(session)) }
        /** Updates mutable settings; JSON preserves explicit null and evolving backend options. */
        public fun updateSession(session: JsonObject, eventId: String? = null): LiveEvent =
            event("session.update", eventId) { put("session", session) }
        /** Sends base64 raw audio on a primary WebSocket, after session.started. */
        public fun appendAudio(audio: String, eventId: String? = null): LiveEvent = event("session.input_audio.append", eventId) { put("audio", audio) }
        /** Mutes input audio without ending the session. */
        public fun muteInput(eventId: String? = null): LiveEvent = event("session.input_audio.mute", eventId)
        /** Unmutes input audio. */
        public fun unmuteInput(eventId: String? = null): LiveEvent = event("session.input_audio.unmute", eventId)
        /** Plays a base64 fixture encoded as mono PCM16LE at 24 kHz, regardless of session audio settings. */
        public fun playAudio(audio: String, eventId: String? = null): LiveEvent = event("session.output_audio.fixture.play", eventId) { put("audio", audio) }
        /** Appends developer context; null delegationId applies it to the general session. */
        public fun appendInstructions(content: String, delegationId: String? = null, eventId: String? = null): LiveEvent =
            appendContext("session.instructions.append", content, delegationId, eventId)
        /** Supplies silent progress context; it is not a secrecy boundary. */
        public fun appendThinking(content: String, delegationId: String? = null, eventId: String? = null): LiveEvent =
            appendContext("session.thinking.append", content, delegationId, eventId)
        /** Supplies speakable context or a completed client-delegation result. */
        public fun appendCommentary(content: String, delegationId: String? = null, eventId: String? = null): LiveEvent =
            appendContext("session.commentary.append", content, delegationId, eventId)
        /** Evaluates the configured Decision questions. Event IDs must be unique for the session. */
        public fun createDecision(eventId: String): LiveEvent = event("decision.create", eventId)
        /** Adds typed evidence for subsequent Decisions without starting an evaluation. */
        public fun createDecisionItem(item: DecisionInputItem, id: String? = null, eventId: String? = null): LiveEvent = event("decision.item.create", eventId) {
            val json = liveEventJson.encodeToJsonElement<DecisionInputItem>(item).jsonObject
            put("item", if (id == null) json else JsonObject(json + ("id" to JsonPrimitive(id))))
        }
        /** Removes evidence from future Decisions without changing Live voice context. */
        public fun deleteDecisionItem(itemId: String, eventId: String? = null): LiveEvent =
            event("decision.item.delete", eventId) { put("item_id", itemId) }
        /** Adds an item to the Responses backend; a response must be requested separately. */
        public fun createResponseItem(item: ResponseInputEntry, eventId: String? = null): LiveEvent =
            event("response.item.create", eventId) { put("item", liveEventJson.encodeToJsonElement(item)) }
        /** Requests or continues work from the Responses backend after supplying tool outputs. */
        public fun createResponse(eventId: String? = null): LiveEvent = event("response.create", eventId)
        /** Gracefully ends a Live session. Continue collecting until session.closed for final usage. */
        public fun closeSession(eventId: String? = null): LiveEvent = event("session.close", eventId)

        private fun appendContext(type: String, content: String, delegationId: String?, eventId: String?) = event(type, eventId) {
            put("content", content)
            put("delegation_id", delegationId?.let(::JsonPrimitive) ?: JsonNull)
        }
        private fun event(type: String, eventId: String? = null, body: JsonObjectBuilder.() -> Unit = {}): LiveEvent =
            LiveEvent(buildJsonObject { put("type", type); eventId?.let { put("event_id", it) }; body() })
    }

    /** Preserves the entire event as its JSON wire representation. */
    public object Serializer : KSerializer<LiveEvent> {
        override val descriptor: SerialDescriptor = JsonObject.serializer().descriptor
        override fun serialize(encoder: Encoder, value: LiveEvent) { encoder.encodeSerializableValue(JsonObject.serializer(), value.payload) }
        override fun deserialize(decoder: Decoder): LiveEvent = LiveEvent(decoder.decodeSerializableValue(JsonObject.serializer()))
    }
}

/** Cumulative audio duration for a Live session, separate from delegated model token usage. */
@Serializable
public data class LiveUsage(
    /** Cumulative billable Live duration in seconds, not an incremental delta. */
    public val seconds: Double,
)

/** A Live service or protocol error. */
@Serializable
public data class LiveError(
    /** Machine-readable error code. */
    public val code: String,
    /** Human-readable error description. */
    public val message: String,
    /** Error category. */
    public val type: String,
    /** Client event responsible for the error, when known. */
    @kotlinx.serialization.SerialName("client_event_id") public val clientEventId: String? = null,
    /** Invalid parameter, when known. */
    public val param: String? = null,
)

private val liveEventJson = Json { ignoreUnknownKeys = true }
