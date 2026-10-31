package com.aallam.openai.api.misc

import com.aallam.openai.api.decision.*
import com.aallam.openai.api.live.*
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlin.test.*

class TestLive {
    @Test
    fun mediaTransportAndBuilderMatchTheCurrentJsonContract() {
        val config = LiveSessionConfig(
            model = ModelId("gpt-live-1"), instructions = "Help the caller.",
            audio = LiveAudioConfig(output = LiveAudioOutput(LiveVoice.Custom("voice_1"))),
            client = LiveClientConfig(LiveDataChannelConfig(
                allowedClientEvents = JsonArray(listOf(JsonPrimitive("session.close"))),
                allowedServerEvents = JsonPrimitive("all"),
            )),
            input = listOf(LiveHistoryItem("user", listOf(LiveHistoryText("Earlier request.")))),
            store = true,
        )
        val request = liveSessionRequest { session = config; transport = LiveTransport.WebRtc("offer") }
        val body = Json.encodeToJsonElement(request).jsonObject
        assertEquals(Json.parseToJsonElement("""{"type":"webrtc","sdp":"offer"}"""), body["transport"])
        val session = body.getValue("session").jsonObject
        assertEquals(Json.parseToJsonElement("""{"output":{"voice":{"id":"voice_1"}}}"""), session["audio"])
        assertEquals(Json.parseToJsonElement("""[{"type":"message","role":"user","content":[{"type":"input_text","text":"Earlier request."}]}]"""), session["input"])
        assertEquals(request, Json.decodeFromJsonElement<LiveSessionRequest>(body))
        assertEquals(JsonPrimitive("marin"), Json.encodeToJsonElement<LiveVoice>(LiveVoice.Name("marin")))
    }

    @Test
    fun nativeDecisionsAndResponsesDelegationsRemainSeparate() {
        val questions = listOf(DecisionQuestion.Predicate(DecisionContent.Text("Is the caller ready?"), "ready"))
        val config = LiveSessionConfig(
            model = ModelId("gpt-live-1"),
            delegation = LiveDelegation.Decisions(LiveDecisionsConfig(ModelId("gpt-6-luna"), questions), 400),
            triggers = listOf(LiveDecisionTrigger("session.input_transcript.delta", LiveDecisionTriggerQuestions(questions))),
        )
        val encoded = Json.encodeToJsonElement(config).jsonObject
        assertEquals("decisions", encoded.getValue("delegation").jsonObject.getValue("type").jsonPrimitive.content)
        assertEquals(400, encoded.getValue("delegation").jsonObject.getValue("acknowledgement_delay_ms").jsonPrimitive.int)
        assertEquals("decisions", encoded.getValue("triggers").jsonArray.single().jsonObject.getValue("type").jsonPrimitive.content)
        assertEquals(config, Json.decodeFromJsonElement<LiveSessionConfig>(encoded))
        val responses = LiveDelegation.Responses(LiveResponsesConfig(
            ModelId("gpt-6-luna"), instructions = "Backend rules.", maxOutputTokens = 16,
            toolChoice = JsonPrimitive("none"), text = LiveResponseText("low"),
        ))
        val body = Json.encodeToJsonElement<LiveDelegation>(responses).jsonObject
        assertEquals("responses", body.getValue("type").jsonPrimitive.content)
        assertEquals(16, body.getValue("responses").jsonObject.getValue("max_output_tokens").jsonPrimitive.int)
        assertEquals(Json.parseToJsonElement("""{"type":"client"}"""), Json.encodeToJsonElement<LiveDelegation>(LiveDelegation.Client()))
    }

    @Test
    fun storedForkInheritsModelAndVoiceWhileSipCredentialsStayRedacted() {
        val start = LiveEvent.startFork(LiveSessionOverrides(audio = LiveAudioConfig(LiveAudioFormat("audio/pcmu", 8000)), store = false))
        assertEquals(Json.parseToJsonElement("""{"type":"session.start","session":{"audio":{"format":{"type":"audio/pcmu","rate":8000}},"store":false}}"""), start.payload)
        assertFailsWith<IllegalArgumentException> { LiveAudioFormat("audio/pcm", 8000) }
        assertFailsWith<IllegalArgumentException> { LiveSessionOverrides(audio = LiveAudioConfig(output = LiveAudioOutput(LiveVoice.Name("marin")))) }
        assertFailsWith<IllegalArgumentException> { LiveSessionRequest(LiveSessionConfig(ModelId("gpt-live-1"), audio = LiveAudioConfig(LiveAudioFormat())), LiveTransport.WebRtc("offer")) }
        val credentials = LiveSipAuthentication("user", "secret")
        assertFalse(credentials.toString().contains("secret"))
        val sip = LiveTransport.Sip("+12025550123", LiveSipTrunk(credentials, "+12025550124", "sips:trunk.example.com"))
        val json = Json.encodeToJsonElement<LiveTransport>(sip).jsonObject
        assertEquals("digest", json.getValue("trunk").jsonObject.getValue("auth").jsonObject.getValue("type").jsonPrimitive.content)
        assertFailsWith<IllegalArgumentException> { LiveSessionForkRequest(sip) }
        assertEquals(LiveTransportResult.Sip, Json.decodeFromString<LiveTransportResult>("""{"type":"sip"}"""))
    }

    @Test
    fun protocolHelpersPreserveRequiredNullFieldsAndUnknownPayloads() {
        val context = LiveEvent.appendCommentary("The order is ready.")
        assertEquals(JsonNull, context.payload["delegation_id"])
        assertEquals(JsonPrimitive("The order is ready."), context.payload["content"])
        val audio = LiveEvent.appendAudio("AQID", "audio_1")
        assertEquals("session.input_audio.append", audio.type)
        assertEquals("audio_1", audio.eventId)
        val event = Json.decodeFromString<LiveEvent>("""{
            "type":"session.usage.updated","event_id":"evt_1","usage":{"seconds":2.5},
            "context_window":{"usage_ratio":0.25},"future":{"retained":true}
        }""")
        assertEquals(2.5, event.usage!!.seconds)
        assertEquals(0.25, event.contextUsageRatio)
        assertEquals(event, Json.decodeFromString<LiveEvent>(Json.encodeToString(event)))
        assertNotNull(event.payload["future"])
        val error = Json.decodeFromString<LiveEvent>("""{"type":"error","error":{"code":"unknown_parameter","type":"invalid_request_error","message":"Unknown field","client_event_id":"client_1"}}""")
        assertEquals("client_1", error.error!!.clientEventId)
        assertEquals("unknown_parameter", error.error!!.code)
    }

    @Test
    fun nestedDecisionResponsesRetainNativeTypedAnswersAndTokenUsage() {
        val event = Json.decodeFromString<LiveEvent>("""{"type":"decision.event","delegation_id":null,"event":{"type":"decision.completed","decision":{
            "model":"gpt-6-luna","answers":[{"type":"predicate","name":"ready","probability":0.9}],
            "usage":{"input_tokens":10,"input_tokens_details":{"cached_tokens":0,"cache_write_tokens":0},"output_tokens":0,"output_tokens_details":{"reasoning_tokens":0},"total_tokens":10}
        }}}""")
        assertNull(event.delegationId)
        assertEquals(0.9, assertIs<DecisionAnswer.Predicate>(event.decision!!.answers.single()).probability)
        assertEquals(10, event.decision!!.usage.totalTokens)
        assertEquals("session.close", LiveEvent.closeSession().type)
    }
}
