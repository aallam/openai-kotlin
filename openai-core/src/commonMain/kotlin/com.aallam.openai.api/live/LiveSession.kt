@file:OptIn(kotlinx.serialization.ExperimentalSerializationApi::class)

package com.aallam.openai.api.live

import com.aallam.openai.api.OpenAIDsl
import com.aallam.openai.api.chat.Effort
import com.aallam.openai.api.decision.DecisionQuestion
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.response.ResponseToolDefinition
import kotlinx.serialization.*
import kotlinx.serialization.json.*
import kotlin.jvm.JvmInline

/** Startup configuration shared by Live media sessions and primary WebSockets. */
@Serializable
public data class LiveSessionConfig(
    /** Live model. It belongs in the session body, never a connection query parameter. */
    public val model: ModelId,
    /** Conversation style and delegation instructions. */
    public val instructions: String? = null,
    /** Voice and, for WebSockets only, the audio wire format. */
    public val audio: LiveAudioConfig? = null,
    /** Startup capabilities for an untrusted WebRTC frontend. */
    public val client: LiveClientConfig? = null,
    /** Backend owner. Omission selects application-managed delegation. */
    public val delegation: LiveDelegation? = null,
    /** Ordered text-only history, supplied before startup. */
    public val input: List<LiveHistoryItem>? = null,
    /** Enables stored-session forking and recording downloads. */
    public val store: Boolean? = null,
    /** Independent Decisions evaluations triggered by transcript deltas. */
    public val triggers: List<LiveDecisionTrigger>? = null,
)

/** Creates a WebRTC session or an outbound SIP call. SIP requests must not be blindly retried. */
@Serializable
public data class LiveSessionRequest(
    /** Initial conversation configuration. */
    public val session: LiveSessionConfig,
    /** WebRTC offer or SIP destination and per-call trunk credentials. */
    public val transport: LiveTransport,
) {
    init { require(session.audio?.format == null) { "WebRTC and SIP negotiate audio formats on the media transport" } }
}

/** Builds a Live media-session request. */
public fun liveSessionRequest(block: LiveSessionRequestBuilder.() -> Unit): LiveSessionRequest =
    LiveSessionRequestBuilder().apply(block).build()

/** Builder for [LiveSessionRequest]. */
@OpenAIDsl
public class LiveSessionRequestBuilder {
    /** Initial conversation configuration. */
    public var session: LiveSessionConfig? = null
    /** WebRTC offer or outbound SIP transport. */
    public var transport: LiveTransport? = null
    /** Builds the request, requiring both the session and transport. */
    public fun build(): LiveSessionRequest = LiveSessionRequest(
        requireNotNull(session) { "session is required" }, requireNotNull(transport) { "transport is required" },
    )
}

/** WebRTC or SIP settings for creating a Live media session. */
@Serializable
public sealed interface LiveTransport {
    /** WebRTC SDP offer. The caller owns the peer connection and media tracks. */
    @Serializable @SerialName("webrtc")
    public data class WebRtc(
        /** SDP offer to exchange for an answer. */
        public val sdp: String,
    ) : LiveTransport

    /** Places a real outbound SIP call using a configured provider trunk. */
    @Serializable @SerialName("sip")
    public data class Sip(
        /** E.164 destination number. */
        public val destination: String,
        /** Per-call provider configuration, never returned by the creation response. */
        public val trunk: LiveSipTrunk,
    ) : LiveTransport
}

/** Outbound SIP trunk configuration. Keep it on the server and avoid logging request bodies. */
@Serializable
public data class LiveSipTrunk(
    /** SIP Digest credentials. */
    public val auth: LiveSipAuthentication,
    /** E.164 caller number. */
    @SerialName("caller_number") public val callerNumber: String,
    /** TLS provider URI, such as `sips:provider.example.com`. */
    @SerialName("provider_url") public val providerUrl: String,
)

/** SIP Digest credentials. The password is redacted from [toString]. */
@Serializable
public class LiveSipAuthentication(
    /** Provider username. */
    public val username: String,
    /** Provider password. */
    public val password: String,
) {
    /** SIP authentication mechanism. */
    @EncodeDefault public val type: String = "digest"
    override fun toString(): String = "LiveSipAuthentication(username=$username, password=<redacted>, type=$type)"
}

/** Newly created session and its negotiated transport. */
@Serializable
public data class LiveSessionResult(
    /** Opaque identifier used for controls, recording and sideband connections. */
    public val session: LiveSessionReference,
    /** SDP answer for WebRTC, or a SIP transport marker. */
    public val transport: LiveTransportResult,
)

/** Opaque identifier of a Live session. */
@Serializable
public data class LiveSessionReference(
    /** Preserve the entire value, including its prefix. */
    public val id: String,
)

/** Negotiated media transport; outbound SIP credentials are never part of this result. */
@Serializable
public sealed interface LiveTransportResult {
    /** WebRTC answer. */
    @Serializable @SerialName("webrtc")
    public data class WebRtc(
        /** Answer SDP to apply to the caller-owned peer connection. */
        public val sdp: String,
    ) : LiveTransportResult
    /** SIP initialization has completed, but the remote party may not have answered yet. */
    @Serializable @SerialName("sip")
    public data object Sip : LiveTransportResult
}

/** Forks a stored session into a new WebRTC connection. */
@Serializable
public data class LiveSessionForkRequest(
    /** WebRTC offer for the new connection. */
    public val transport: LiveTransport,
    /** Omitted fields inherit the stored configuration. A new model is not allowed. */
    public val session: LiveSessionOverrides? = null,
) {
    init {
        require(transport is LiveTransport.WebRtc) { "HTTP Live session forks require WebRTC transport" }
        require(session?.audio == null) { "WebRTC forks negotiate their audio format through SDP" }
    }
}

/** Configuration overrides for stored-session forks; the source model is inherited. */
@Serializable
public data class LiveSessionOverrides(
    /** New WebSocket audio format; not accepted by HTTP WebRTC forks. */
    public val audio: LiveAudioConfig? = null,
    /** New WebRTC frontend permissions, when applicable. */
    public val client: LiveClientConfig? = null,
    /** Responses backend overrides without changing delegation ownership. */
    public val delegation: LiveResponsesDelegationUpdate? = null,
    /** Overrides whether the new session is stored. */
    public val store: Boolean? = null,
) {
    init { require(audio?.output == null) { "A fork inherits its stored voice; only the WebSocket audio format can be overridden" } }
}

/** Changes a Responses backend on a fork without changing the delegation type. */
@Serializable
public data class LiveResponsesDelegationUpdate(
    /** Responses backend fields. JSON preserves explicit null and evolving nested unions. */
    public val responses: JsonObject? = null,
    /** Milliseconds before automatic acknowledgement, when configured. */
    @SerialName("acknowledgement_delay_ms") public val acknowledgementDelayMs: Int? = null,
) {
    /** Delegation owner, which remains Responses. */
    @EncodeDefault public val type: String = "responses"
}

/** Live audio settings. Only a primary or fork WebSocket accepts [format]. */
@Serializable
public data class LiveAudioConfig(
    /** Audio wire format; media transports negotiate it independently. */
    public val format: LiveAudioFormat? = null,
    /** Speech generation settings. */
    public val output: LiveAudioOutput? = null,
)

/** Raw mono audio encoding for a Live WebSocket. */
@Serializable
public data class LiveAudioFormat(
    /** `audio/pcm`, `audio/pcmu` or `audio/pcma`. */
    @EncodeDefault public val type: String = "audio/pcm",
    /** PCM supports 16000/24000 Hz; G.711 supports 8000 Hz. */
    @EncodeDefault public val rate: Int = 24000,
) {
    init {
        require(when (type) {
            "audio/pcm" -> rate == 16000 || rate == 24000
            "audio/pcmu", "audio/pcma" -> rate == 8000
            else -> false
        }) { "Unsupported Live audio encoding or sample rate" }
    }
}

/** Voice settings, selected before startup and immutable afterward. */
@Serializable
public data class LiveAudioOutput(
    /** Built-in name or an existing custom voice ID. */
    public val voice: LiveVoice? = null,
)

/** A built-in voice name or custom voice reference. */
@Serializable(with = LiveVoiceSerializer::class)
public sealed interface LiveVoice {
    /** Built-in voice name; arbitrary values permit new server-supported voices. */
    @Serializable @JvmInline
    public value class Name(public val value: String) : LiveVoice
    /** Previously enrolled custom voice. */
    @Serializable
    public data class Custom(
        /** Existing custom voice identifier. */
        public val id: String,
    ) : LiveVoice
}

internal object LiveVoiceSerializer : JsonContentPolymorphicSerializer<LiveVoice>(LiveVoice::class) {
    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<LiveVoice> = when {
        element is JsonPrimitive && element.isString -> LiveVoice.Name.serializer()
        element is JsonObject -> LiveVoice.Custom.serializer()
        else -> throw SerializationException("Live voice must be a name or custom voice reference")
    }
}

/** Startup permissions for an untrusted WebRTC frontend. Sideband credentials remain trusted. */
@Serializable
public data class LiveClientConfig(
    /** Per-event permissions for the frontend data channel. */
    @SerialName("data_channel") public val dataChannel: LiveDataChannelConfig,
)

/** Frontend event restrictions. Use `"all"` or arrays of selectors. */
@Serializable
public data class LiveDataChannelConfig(
    /** `"all"` or an array of allowed client event names. An empty array allows none. */
    @SerialName("allowed_client_events") public val allowedClientEvents: JsonElement? = null,
    /** `"all"` or selector objects; Responses selectors also include `response_event`. */
    @SerialName("allowed_server_events") public val allowedServerEvents: JsonElement? = null,
)

/** Owner of backend work delegated by the Live model. */
@Serializable
public sealed interface LiveDelegation {
    /** The caller handles delegation events using its own backend. */
    @Serializable @SerialName("client")
    public data class Client(
        /** Automatic acknowledgement delay in milliseconds. */
        @SerialName("acknowledgement_delay_ms") public val acknowledgementDelayMs: Int? = null,
    ) : LiveDelegation
    /** OpenAI runs a Responses backend; the application still executes its own function tools. */
    @Serializable @SerialName("responses")
    public data class Responses(
        /** Responses model, prompt and tools. */
        public val responses: LiveResponsesConfig,
        /** Automatic acknowledgement delay in milliseconds. */
        @SerialName("acknowledgement_delay_ms") public val acknowledgementDelayMs: Int? = null,
    ) : LiveDelegation
    /** Evaluates native Decisions questions for delegated work. */
    @Serializable @SerialName("decisions")
    public data class Decisions(
        /** Decisions model and ordered questions. */
        public val decisions: LiveDecisionsConfig,
        /** Automatic acknowledgement delay in milliseconds. */
        @SerialName("acknowledgement_delay_ms") public val acknowledgementDelayMs: Int? = null,
    ) : LiveDelegation
}

/** Backend configuration for Live Responses delegation. */
@Serializable
public data class LiveResponsesConfig(
    /** Backend Responses model, independent of the Live voice model. */
    public val model: ModelId,
    /** Backend instructions. */
    public val instructions: String? = null,
    /** Maximum generated tokens for each delegation. */
    @SerialName("max_output_tokens") public val maxOutputTokens: Int? = null,
    /** Allows multiple tool calls in one response when supported. */
    @SerialName("parallel_tool_calls") public val parallelToolCalls: Boolean? = null,
    /** Backend reasoning options. */
    public val reasoning: LiveResponseReasoning? = null,
    /** Backend service tier. */
    @SerialName("service_tier") public val serviceTier: String? = null,
    /** Backend verbosity. */
    public val text: LiveResponseText? = null,
    /** Native Responses tool-choice string or object. */
    @SerialName("tool_choice") public val toolChoice: JsonElement? = null,
    /** Native Responses tools; evolving nested tool shapes retain their JSON fields. */
    public val tools: List<ResponseToolDefinition>? = null,
)

/** Reasoning controls for a Responses backend. */
@Serializable
public data class LiveResponseReasoning(
    /** Reasoning effort supported by the selected model. */
    public val effort: Effort? = null,
    /** Reasoning summary detail. */
    public val summary: String? = null,
)

/** Text output settings for a Responses backend. */
@Serializable
public data class LiveResponseText(
    /** Requested response verbosity. */
    public val verbosity: String? = null,
)

/** Model and questions used by Decisions delegation. */
@Serializable
public data class LiveDecisionsConfig(
    /** Decisions model. */
    public val model: ModelId,
    /** Native Decisions questions, evaluated together in order. */
    public val questions: List<DecisionQuestion>,
) {
    init { require(questions.size in 1..64) { "Live Decisions delegation requires between 1 and 64 questions" } }
}

/** A Decisions trigger independent of normal delegation. */
@Serializable
public data class LiveDecisionTrigger(
    /** Input or output transcript-delta event name. */
    @SerialName("event_type") public val eventType: String,
    /** Questions evaluated against the transcript and shared Decision items. */
    public val decisions: LiveDecisionTriggerQuestions,
) {
    /** Trigger backend type. */
    @EncodeDefault public val type: String = "decisions"
}

/** Questions evaluated by a transcript trigger. */
@Serializable
public data class LiveDecisionTriggerQuestions(
    /** Between one and 64 native Decisions questions. */
    public val questions: List<DecisionQuestion>,
) {
    init { require(questions.size in 1..64) { "Live transcript triggers require between 1 and 64 questions" } }
}

/** One text-only initial history message. */
@Serializable
public data class LiveHistoryItem(
    /** `developer`, `user` or `assistant`. */
    public val role: String,
    /** Exactly one role-appropriate text part. */
    public val content: List<LiveHistoryText>,
    /** Optional history identifier. */
    public val id: String? = null,
    /** Optional supplied message status. */
    public val status: String? = null,
) {
    /** History item type. */
    @EncodeDefault public val type: String = "message"
    init {
        require(role in setOf("developer", "user", "assistant")) { "Unsupported Live history role" }
        require(content.size == 1) { "Live history messages require exactly one text part" }
        require(if (role == "assistant") content.single().type in setOf("text", "output_text") else content.single().type == "input_text") {
            "Unsupported Live history content type for role $role"
        }
    }
}

/** A single text part in initial Live history. */
@Serializable
public data class LiveHistoryText(
    /** Text supplied as initial context. */
    public val text: String,
    /** `input_text` for user/developer; `text` or `output_text` for assistant history. */
    @EncodeDefault public val type: String = "input_text",
)
