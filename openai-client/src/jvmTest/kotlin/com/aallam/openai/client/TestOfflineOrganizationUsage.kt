package com.aallam.openai.client

import com.aallam.openai.api.admin.usage.*
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.exception.PermissionException
import com.aallam.openai.api.logging.LogLevel
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlin.test.*

class TestOfflineOrganizationUsage {
    @Test
    fun allReportsPreservePathsFiltersCursorAndTypedResults() = runTest {
        val paths = listOf(
            "usage/audio_speeches", "usage/audio_transcriptions", "usage/code_interpreter_sessions",
            "usage/completions", "costs", "usage/decisions", "usage/embeddings", "usage/file_search_calls",
            "usage/images", "usage/moderations", "usage/vector_stores", "usage/web_search_calls",
        )
        var index = 0
        val engine = MockEngine { request ->
            assertEquals(HttpMethod.Get, request.method)
            assertEquals("/v1/organization/${paths[index++]}", request.url.encodedPath)
            assertEquals("100", request.url.parameters["start_time"])
            assertEquals("200", request.url.parameters["end_time"])
            assertEquals("next", request.url.parameters["page"])
            assertEquals("1d", request.url.parameters["bucket_width"])
            assertEquals("1", request.url.parameters["limit"])
            assertEquals(listOf("project_id"), request.url.parameters.getAll("group_by[]"))
            assertEquals(listOf("proj_1", "proj_2"), request.url.parameters.getAll("project_ids[]"))
            assertEquals("test", request.headers["X-Report"])
            respond(pageJson, headers = jsonHeaders)
        }
        OpenAIAdmin(config(engine)).use { client ->
            val calls = listOf(client::organizationAudioSpeechesUsage, client::organizationAudioTranscriptionsUsage,
                client::organizationCodeInterpreterSessionsUsage, client::organizationCompletionsUsage,
                client::organizationCosts, client::organizationDecisionsUsage, client::organizationEmbeddingsUsage,
                client::organizationFileSearchCallsUsage, client::organizationImagesUsage, client::organizationModerationsUsage,
                client::organizationVectorStoresUsage, client::organizationWebSearchCallsUsage)
            for (call in calls) {
                val page = call(OrganizationUsageQuery(100, endTime = 200, page = "next", bucketWidth = "1d", limit = 1,
                    groupBy = listOf("project_id"), projectIds = listOf("proj_1", "proj_2")),
                    RequestOptions(headers = mapOf("X-Report" to "test")))
                assertTrue(page.hasMore)
                assertEquals("page_2", page.nextPage)
                val bucket = page.data.single()
                assertEquals(100L, bucket.startTime)
                assertEquals(200L, bucket.endTime)
                assertEquals(12, bucket.results.size)
                val completions = assertIs<OrganizationUsageResult.Completions>(bucket.results[0])
                assertEquals(3000000000L, completions.inputTokens)
                assertEquals(4L, completions.inputCacheWrite30mTokens)
                val decisions = assertIs<OrganizationUsageResult.Decisions>(bucket.results[1])
                assertEquals("service", decisions.serviceTier)
                val costs = assertIs<OrganizationUsageResult.Costs>(bucket.results.last())
                assertEquals(0.25, costs.amount?.value)
                assertEquals("tokens", costs.quantityUnit)
                assertEquals("usd", costs.amount?.currency)
            }
        }
        assertEquals(12, index)
    }

    @Test
    fun endpointSpecificArrayAndBooleanFiltersAreRetained() = runTest {
        val engine = MockEngine { request ->
            val query = request.url.parameters
            when (request.url.encodedPath.substringAfterLast('/')) {
                "completions", "decisions" -> {
                    assertEquals("false", query["batch"])
                    assertEquals(listOf("key_1"), query.getAll("api_key_ids[]"))
                    assertEquals(listOf("model_1"), query.getAll("models[]"))
                    assertEquals(listOf("user_1"), query.getAll("user_ids[]"))
                }
                "images" -> {
                    assertEquals(listOf("1024x1024"), query.getAll("sizes[]"))
                    assertEquals(listOf("image.edit"), query.getAll("sources[]"))
                }
                "file_search_calls" -> assertEquals(listOf("vs_1"), query.getAll("vector_store_ids[]"))
                "web_search_calls" -> assertEquals(listOf("low"), query.getAll("context_levels[]"))
                "costs" -> assertEquals(listOf("exact, line item"), query.getAll("line_items[]"))
                else -> error("Unexpected endpoint")
            }
            respond(pageJson, headers = jsonHeaders)
        }
        OpenAIAdmin(config(engine)).use { client ->
            val query = OrganizationUsageQuery(100, batch = false, apiKeyIds = listOf("key_1"), models = listOf("model_1"), userIds = listOf("user_1"))
            client.organizationCompletionsUsage(query)
            client.organizationDecisionsUsage(query)
            client.organizationImagesUsage(OrganizationUsageQuery(100, sizes = listOf("1024x1024"), sources = listOf("image.edit")))
            client.organizationFileSearchCallsUsage(OrganizationUsageQuery(100, vectorStoreIds = listOf("vs_1")))
            client.organizationWebSearchCallsUsage(OrganizationUsageQuery(100, contextLevels = listOf("low")))
            client.organizationCosts(OrganizationUsageQuery(100, lineItems = listOf("exact, line item")))
            assertFailsWith<IllegalArgumentException> { client.organizationVectorStoresUsage(query) }
        }
    }

    @Test
    fun adminPermissionErrorsPropagateAndCustomClientsRemainOptional() = runTest {
        OpenAIAdmin(config(MockEngine { respond("""{"error":{"message":"Admin key required","type":"permission_error"}}""",
            HttpStatusCode.Forbidden, jsonHeaders) })).use { client ->
            assertFailsWith<PermissionException> { client.organizationCosts(OrganizationUsageQuery(100)) }
        }
        val custom = object : OpenAIAdmin { override fun close() {} }
        assertFailsWith<UnsupportedOperationException> { custom.organizationCosts(OrganizationUsageQuery(100)) }
    }

    private fun config(engine: MockEngine) = OpenAIConfig("test-admin", engine = engine,
        retry = RetryStrategy(maxRetries = 0), logging = LoggingConfig(LogLevel.None))
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val pageJson = """{"object":"page","has_more":true,"next_page":"page_2","data":[{
        "object":"bucket","start_time":100,"end_time":200,"results":[
            {"object":"organization.usage.completions.result","input_tokens":3000000000,"output_tokens":2,"num_model_requests":1,"input_cache_write_30m_tokens":4},
            {"object":"organization.usage.decisions.result","input_tokens":3,"output_tokens":0,"num_model_requests":1,"service_tier":"service"},
            {"object":"organization.usage.embeddings.result","input_tokens":3,"num_model_requests":1},
            {"object":"organization.usage.moderations.result","input_tokens":3,"num_model_requests":1},
            {"object":"organization.usage.images.result","images":1,"num_model_requests":1},
            {"object":"organization.usage.audio_speeches.result","characters":5,"num_model_requests":1},
            {"object":"organization.usage.audio_transcriptions.result","seconds":1,"num_model_requests":1},
            {"object":"organization.usage.vector_stores.result","usage_bytes":4},
            {"object":"organization.usage.code_interpreter_sessions.result","num_sessions":1},
            {"object":"organization.usage.file_searches.result","num_requests":1},
            {"object":"organization.usage.web_searches.result","num_requests":1,"num_model_requests":1},
            {"object":"organization.costs.result","amount":{"currency":"usd","value":0.25},"quantity":10,"quantity_unit":"tokens"}
        ]}]}"""
}
