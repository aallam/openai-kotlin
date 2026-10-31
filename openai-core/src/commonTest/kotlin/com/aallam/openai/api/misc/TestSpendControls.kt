package com.aallam.openai.api.misc

import com.aallam.openai.api.admin.*
import kotlinx.serialization.json.*
import kotlin.test.*

class TestSpendControls {
    @Test
    fun requestsEmitAllRequiredReplacementFieldsAndKeepThresholdsInCents() {
        val limit = spendLimitRequest { thresholdAmount = 12345678901L }
        assertEquals(Json.parseToJsonElement("""{"threshold_amount":12345678901,"currency":"USD","interval":"month"}"""), Json.encodeToJsonElement(limit))
        val alert = spendAlertRequest {
            thresholdAmount = 0
            notificationChannel = SpendAlertEmailChannel(listOf("finance@example.com"))
        }
        val expected = Json.parseToJsonElement("""{"threshold_amount":0,"currency":"USD","interval":"month","notification_channel":{"recipients":["finance@example.com"],"type":"email"}}""")
        assertEquals(expected, Json.encodeToJsonElement(alert))
        assertEquals(alert, Json.decodeFromJsonElement<SpendAlertRequest>(expected))
        assertFailsWith<IllegalArgumentException> { SpendLimitRequest(0) }
        assertFailsWith<IllegalArgumentException> { SpendLimitRequest(-1) }
        assertFailsWith<IllegalArgumentException> { SpendAlertRequest(-1, SpendAlertEmailChannel(emptyList())) }
    }

    @Test
    fun responsesRetainEnforcementUnknownStatusNullableCursorsAndIdlessDeletions() {
        val limit = Json.decodeFromString<SpendLimit>("""{"threshold_amount":0,"currency":"USD","interval":"month","enforcement":{"status":"inactive"},"object":"organization.spend_limit"}""")
        assertEquals(0L, limit.thresholdAmount)
        assertEquals("inactive", limit.enforcement.status)
        val future = Json.decodeFromString<SpendLimitEnforcement>("""{"status":"new_server_status"}""")
        assertEquals("new_server_status", future.status)
        val page = Json.decodeFromString<SpendAlertPage>("""{"object":"list","data":[],"has_more":false,"first_id":null,"last_id":null}""")
        assertEquals("list", page.objectType)
        assertTrue(page.isEmpty())
        assertNull(page.firstId)
        assertNull(page.lastId)
        val deleted = Json.decodeFromString<SpendLimitDeleted>("""{"deleted":true,"object":"project.spend_limit.deleted"}""")
        assertTrue(deleted.deleted)
        assertEquals("project.spend_limit.deleted", deleted.objectType)
    }
}
