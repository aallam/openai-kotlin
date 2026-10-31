package com.aallam.openai.client

import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.skill.SkillId
import com.aallam.openai.client.internal.awaitListedSkill
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class TestOfflineSkillListing {
    @Test
    fun waitsForAReadableSkillToAppearInTheList() = runTest {
        var calls = 0
        withClient(MockEngine {
            calls++
            respond(if (calls == 1) page() else page("skill_target"), headers = jsonHeaders)
        }) { client ->
            assertEquals(SkillId("skill_target"), awaitListedSkill(client, SkillId("skill_target")).id)
            assertEquals(2, calls)
        }
    }

    @Test
    fun followsCursorsWithAnExplicitSortOrder() = runTest {
        var calls = 0
        withClient(MockEngine { request ->
            calls++
            assertEquals("100", request.url.parameters["limit"])
            assertEquals("desc", request.url.parameters["order"])
            assertEquals(if (calls == 1) null else "skill_other", request.url.parameters["after"])
            respond(if (calls == 1) page("skill_other", true) else page("skill_target"), headers = jsonHeaders)
        }) { client ->
            assertEquals(SkillId("skill_target"), awaitListedSkill(client, SkillId("skill_target")).id)
            assertEquals(2, calls)
        }
    }

    @Test
    fun permissionErrorsPropagateWithoutPolling() = runTest {
        var calls = 0
        withClient(MockEngine {
            calls++
            respond("""{"error":{"message":"Denied","type":"permission_error"}}""", HttpStatusCode.Forbidden, jsonHeaders)
        }) { client ->
            assertFailsWith<OpenAIAPIException> { awaitListedSkill(client, SkillId("skill_target")) }
            assertEquals(1, calls)
        }
    }

    @Test
    fun repeatedCursorsFailWithoutRestartingTheScan() = runTest {
        var calls = 0
        withClient(MockEngine {
            calls++
            respond(page("skill_other", true), headers = jsonHeaders)
        }) { client ->
            assertFailsWith<IllegalStateException> { awaitListedSkill(client, SkillId("skill_target")) }
            assertEquals(2, calls)
        }
    }

    private suspend fun withClient(engine: MockEngine, block: suspend (OpenAI) -> Unit) {
        val client = OpenAI(OpenAIConfig("test", engine = engine, retry = RetryStrategy(maxRetries = 0)))
        try { block(client) } finally { client.close() }
    }

    private fun page(id: String? = null, more: Boolean = false): String {
        val data = id?.let { """{"id":"$it","created_at":1,"default_version":"1","description":"fixture","latest_version":"1","name":"fixture","object":"skill"}""" }.orEmpty()
        val cursor = id?.let { "\"$it\"" } ?: "null"
        return """{"data":[$data],"has_more":$more,"last_id":$cursor}"""
    }

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
}
