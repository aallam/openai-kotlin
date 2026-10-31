package com.aallam.openai.api.misc

import com.aallam.openai.api.voice.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlin.test.*

class TestVoiceManagement {
    @Test
    fun promptRequestIncludesItsDiscriminatorAndOmitsServerDefaults() {
        val built = promptVoiceRequest { name = "Narrator"; prompt = "A calm fictional voice." }
        assertEquals(VoiceCreateRequest.Prompt("Narrator", "A calm fictional voice."), built)
        val actual = Json.encodeToJsonElement(built)
        assertEquals(Json.parseToJsonElement("""{"type":"prompt","name":"Narrator","prompt":"A calm fictional voice."}"""), actual)
        assertEquals(built, Json.decodeFromString<VoiceCreateRequest.Prompt>(Json.encodeToString(built)))
        val update = voiceConsentUpdateRequest { name = "New label" }
        assertEquals(Json.parseToJsonElement("""{"name":"New label"}"""), Json.encodeToJsonElement(update))
    }

    @Test
    fun consentPageRetainsEnvelopeAndNullableCursorMetadata() {
        val actual = Json.decodeFromString<VoiceConsentPage>("""{
            "object":"list","data":[{"id":"cons_1","created_at":1700000000,"language":"en-US","name":"Speaker","object":"audio.voice_consent"}],
            "has_more":false,"first_id":null,"last_id":null
        }""")
        assertEquals("list", actual.objectType)
        assertFalse(actual.hasMore)
        assertNull(actual.firstId)
        assertNull(actual.lastId)
        assertEquals(1700000000L, actual.single().createdAt)
        assertEquals("en-US", actual.single().language)
    }
}
