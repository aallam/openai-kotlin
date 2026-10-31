package com.aallam.openai.api.misc

import com.aallam.openai.api.provenance.*
import com.aallam.openai.api.safety.*
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.*

class TestSafetyAndProvenance {
    @Test
    fun provenancePreservesBothSignalTypesNullableMetadataAndRfc3339Time() {
        val result = Json.decodeFromString<ContentProvenanceCheck>("""{
            "object":"content_provenance_check","created_at":1787659200,
            "results":[
                {"type":"c2pa","outcome":"detected","validation_state":"trusted","issuer":"OpenAI OpCo, LLC","model":"gpt-image","generated_at":"2026-07-27T18:34:12Z"},
                {"type":"synthid","outcome":"not_detected","model":null,"generated_at":null}
            ]
        }""")
        val c2pa = assertIs<ProvenanceResult.C2pa>(result.results[0])
        assertEquals("trusted", c2pa.validationState)
        assertEquals("OpenAI OpCo, LLC", c2pa.issuer)
        assertEquals("gpt-image", c2pa.model)
        assertEquals("2026-07-27T18:34:12Z", c2pa.generatedAt)
        val synthId = assertIs<ProvenanceResult.SynthId>(result.results[1])
        assertEquals("not_detected", synthId.outcome)
        assertNull(synthId.model)
        assertNull(synthId.generatedAt)
        assertEquals(result, Json.decodeFromString<ContentProvenanceCheck>(Json.encodeToString(result)))
        val absent = Json.decodeFromString<ProvenanceResult>("""{"type":"c2pa","outcome":"not_detected","validation_state":"not_present","issuer":null,"model":null,"generated_at":null}""")
        assertNull(assertIs<ProvenanceResult.C2pa>(absent).issuer)
    }

    @Test
    fun safetyMetadataRetainsNullableReasonsAndOptionalExplanations() {
        val alert = Json.decodeFromString<SafetyAlert>("""{
            "id":"alert_1","created_at":1787659200,"error_type":"potentially_unintended_data_access",
            "model":"gpt-6-astra","request_id":"req_1","response_id":"resp_1","request_paused":true,"reason":null
        }""")
        assertTrue(alert.requestPaused)
        assertNull(alert.reason)
        assertNull(alert.detailedExplanation)
        val explained = alert.copy(detailedExplanation = "A temporary customer-safe explanation.")
        assertEquals(explained, Json.decodeFromString<SafetyAlert>(Json.encodeToString(explained)))
        val case = Json.decodeFromString<SafetyCase>("""{
            "id":"C-1","created_at":1787659200,"entity_identifier":"entity-1","notice":{"type":"warning"},"reason":null
        }""")
        assertEquals("warning", case.notice.type)
        assertNull(case.reason)
        assertEquals(case, Json.decodeFromString<SafetyCase>(Json.encodeToString(case)))
    }
}
