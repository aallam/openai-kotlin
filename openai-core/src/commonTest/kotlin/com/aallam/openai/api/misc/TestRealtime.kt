package com.aallam.openai.api.misc

import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.realtime.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlin.test.*

class TestRealtime {
    @Test
    fun gaSessionDiscriminatorAndExplicitVadNullAreSent() {
        val session = RealtimeSessionConfig(
            model = ModelId("realtime-model"),
            audio = RealtimeAudioConfig(input = RealtimeAudioInput(turnDetection = JsonNull)),
        )
        val json = Json.encodeToJsonElement(session).jsonObject
        assertEquals("realtime", json["type"]!!.jsonPrimitive.content)
        assertEquals(JsonNull, json["audio"]!!.jsonObject["input"]!!.jsonObject["turn_detection"])
        val function = Json.encodeToJsonElement(RealtimeFunctionTool("weather")).jsonObject
        assertEquals("function", function["type"]!!.jsonPrimitive.content)
    }

    @Test
    fun futureEventFieldsArePreservedAcrossDecodeEncode() {
        val raw = """{"type":"future.event","event_id":"evt_1","future_field":{"values":[1,2,3]}}"""
        val event = Json.decodeFromString<RealtimeEvent>(raw)
        assertEquals("future.event", event.type)
        assertEquals("evt_1", event.eventId)
        assertEquals(Json.parseToJsonElement(raw), Json.parseToJsonElement(Json.encodeToString(event)))
        assertFailsWith<IllegalArgumentException> { RealtimeEvent(buildJsonObject {}) }
    }

    @Test
    fun functionResultsAndTranslationCloseHaveTheirProtocolShapes() {
        val result = RealtimeEvent.functionOutput("call_1", "answer").payload
        assertEquals("conversation.item.create", result["type"]!!.jsonPrimitive.content)
        assertEquals("function_call_output", result["item"]!!.jsonObject["type"]!!.jsonPrimitive.content)
        assertEquals("session.close", RealtimeEvent.closeTranslation().type)
        assertFailsWith<IllegalArgumentException> { RealtimeSecretExpiry(seconds = 1) }
    }
}
