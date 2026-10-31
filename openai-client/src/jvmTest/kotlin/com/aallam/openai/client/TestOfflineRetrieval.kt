package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.file.*
import com.aallam.openai.api.batch.BatchCreateRequest
import com.aallam.openai.api.batch.CompletionWindow
import com.aallam.openai.api.core.Endpoint
import kotlinx.io.Buffer
import com.aallam.openai.api.vectorstore.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.http.content.TextContent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import kotlin.test.*

class TestOfflineRetrieval {
    @Test
    fun filePagePreservesMetadataAndFilters() = runTest {
        val engine = MockEngine { request ->
            assertEquals("/v1/files", request.url.encodedPath)
            assertEquals("file-previous", request.url.parameters["after"])
            assertEquals("10", request.url.parameters["limit"])
            assertEquals("asc", request.url.parameters["order"])
            assertEquals("batch", request.url.parameters["purpose"])
            respond("""{"data":[],"has_more":true,"first_id":"file-first","last_id":"file-last"}""", headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        OpenAI(OpenAIConfig(token = "test", engine = engine)).use { client ->
            val page = client.filesPage(FileId("file-previous"), 10, SortOrder.Ascending, Purpose("batch"))
            assertTrue(page.hasMore == true)
            assertEquals("file-last", page.lastId)
        }
    }

    @Test
    fun vectorSearchUsesIndependentEndpointAndPreservesHeaderOverride() = runTest {
        var calls = 0
        val engine = MockEngine { request ->
            assertEquals("/v1/vector_stores/vs-1/search", request.url.encodedPath)
            assertEquals(if (calls++ == 0) null else "custom=v1", request.headers["OpenAI-Beta"])
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertEquals("pricing", body["query"]!!.jsonArray.single().jsonPrimitive.content)
            respond("""{"data":[],"has_more":false,"next_page":null,"search_query":["pricing"]}""", headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        OpenAI(OpenAIConfig(token = "test", engine = engine)).use { client ->
            val request = VectorStoreSearchRequest(listOf("pricing"))
            client.searchVectorStore(VectorStoreId("vs-1"), request)
            client.searchVectorStore(VectorStoreId("vs-1"), request, RequestOptions(headers = mapOf("OpenAI-Beta" to "custom=v1")))
        }
        assertEquals(2, calls)
    }

    @Test
    fun correctedFileCursorsAndNullableAttributeUpdatesReachTransport() = runTest {
        var calls = 0
        val engine = MockEngine { request ->
            if (calls++ == 0) {
                assertEquals("file-after", request.url.parameters["after"])
                respond("""{"data":[],"has_more":false}""", headers = headersOf(HttpHeaders.ContentType, "application/json"))
            } else {
                assertEquals("/v1/vector_stores/vs-1/files/file-1", request.url.encodedPath)
                assertEquals(JsonNull, Json.parseToJsonElement((request.body as TextContent).text).jsonObject["attributes"])
                respond("""{"id":"file-1","created_at":0,"vector_store_id":"vs-1","status":"completed"}""", headers = headersOf(HttpHeaders.ContentType, "application/json"))
            }
        }
        OpenAI(OpenAIConfig(token = "test", engine = engine)).use { client ->
            client.vectorStoreFilesPage(VectorStoreId("vs-1"), after = FileId("file-after"))
            client.updateVectorStoreFile(VectorStoreId("vs-1"), FileId("file-1"), null)
        }
    }
    @Test
    fun vectorUpdatesExcludeCreateOnlyFieldsAndSupportClearing() = runTest {
        var calls = 0
        val engine = MockEngine { request ->
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertFalse("file_ids" in body)
            assertFalse("description" in body)
            if (calls++ == 0) assertEquals("renamed", body["name"]?.jsonPrimitive?.content)
            else assertEquals(buildJsonObject { put("expires_after", JsonNull) }, body)
            respond("""{"id":"vs-1","created_at":0,"usage_bytes":0,"file_counts":{"in_progress":0,"completed":0,"failed":0,"cancelled":0,"total":0},"status":"completed"}""", headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        OpenAI(OpenAIConfig(token = "test", engine = engine)).use { client ->
            client.updateVectorStore(VectorStoreId("vs-1"), VectorStoreRequest(fileIds = listOf(FileId("ignored")), name = "renamed"))
            client.updateVectorStoreFields(VectorStoreId("vs-1"), VectorStoreUpdateRequest(clearExpiresAfter = true))
        }
    }

    @Test
    fun richUploadKeepsExpiryOnTheWireAndInResults() = runTest {
        val engine = MockEngine { request ->
            val body = request.body.toByteArray().decodeToString()
            assertTrue(body.contains("expires_after[anchor]"))
            assertTrue(body.contains("created_at"))
            assertTrue(body.contains("expires_after[seconds]"))
            assertTrue(body.contains("3600"))
            respond("""{"id":"file-1","bytes":0,"created_at":0,"filename":"data.jsonl","purpose":"batch","expires_at":3600}""",
                headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        OpenAI(OpenAIConfig(token = "test", engine = engine)).use { client ->
            val file = client.uploadFile(FileUploadRequest(FileSource("data.jsonl", Buffer()), Purpose("batch"), FileExpiration(3600)))
            assertEquals(3600L, file.expiresAt)
            assertEquals(3600L, file.copy().expiresAt)
        }
    }

    @Test
    fun richCreationKeepsVectorOptionsAndReturnedDescription() = runTest {
        val engine = MockEngine { request ->
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertEquals("docs", body["description"]?.jsonPrimitive?.content)
            assertEquals("auto", body["chunking_strategy"]?.jsonObject?.get("type")?.jsonPrimitive?.content)
            respond("""{"id":"vs-1","created_at":0,"usage_bytes":0,"file_counts":{"in_progress":0,"completed":0,"failed":0,"cancelled":0,"total":0},"status":"completed","description":"docs"}""",
                headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        OpenAI(OpenAIConfig(token = "test", engine = engine)).use { client ->
            val store = client.createVectorStoreResource(VectorStoreCreateRequest(description = "docs", chunkingStrategy = ChunkingStrategy()))
            assertEquals("docs", store.description)
            assertEquals("docs", store.copy().description)
        }
    }

    @Test
    fun additiveBatchCreationSendsOutputExpiry() = runTest {
        val engine = MockEngine { request ->
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertEquals(3600, body["output_expires_after"]?.jsonObject?.get("seconds")?.jsonPrimitive?.int)
            respond("""{"id":"batch-1","endpoint":"/v1/responses","errors":null}""",
                headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        OpenAI(OpenAIConfig(token = "test", engine = engine)).use { client ->
            val batch = client.createBatch(BatchCreateRequest(FileId("file-1"), Endpoint.Responses, CompletionWindow.TwentyFourHours, outputExpiresAfter = FileExpiration(3600)))
            assertEquals("batch-1", batch.id.id)
        }
    }

    @Test
    fun fileBatchSendsSharedConfigurationOnlyWithFileIds() = runTest {
        val engine = MockEngine { request ->
            assertEquals("/v1/vector_stores/vs-1/file_batches", request.url.encodedPath)
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertFalse("files" in body)
            assertEquals("file-1", body["file_ids"]!!.jsonArray.single().jsonPrimitive.content)
            assertEquals("docs", body["attributes"]!!.jsonObject["kind"]!!.jsonPrimitive.content)
            assertEquals(800, body["chunking_strategy"]!!.jsonObject["static"]!!.jsonObject["max_chunk_size_tokens"]!!.jsonPrimitive.int)
            respond("""{"id":"batch-1","created_at":0,"vector_store_id":"vs-1","status":"completed","file_counts":{"in_progress":0,"completed":1,"failed":0,"cancelled":0,"total":1}}""",
                headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        OpenAI(OpenAIConfig(token = "test", engine = engine)).use { client ->
            val result = client.createVectorStoreFileBatch(VectorStoreId("vs-1"), VectorStoreFileBatchRequest(
                fileIds = listOf(FileId("file-1")),
                attributes = mapOf("kind" to JsonPrimitive("docs")),
                chunkingStrategy = ChunkingStrategy("static", StaticChunking(800, 200)),
            ))
            assertEquals("batch-1", result.id.id)
        }
    }

    @Test
    fun fileBatchSendsIndividualConfigurationInsideFiles() = runTest {
        val engine = MockEngine { request ->
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertFalse("file_ids" in body)
            assertFalse("attributes" in body)
            assertFalse("chunking_strategy" in body)
            val file = body["files"]!!.jsonArray.single().jsonObject
            assertEquals("file-1", file["file_id"]!!.jsonPrimitive.content)
            assertEquals(true, file["attributes"]!!.jsonObject["reviewed"]!!.jsonPrimitive.boolean)
            assertEquals("auto", file["chunking_strategy"]!!.jsonObject["type"]!!.jsonPrimitive.content)
            respond("""{"id":"batch-1","created_at":0,"vector_store_id":"vs-1","status":"completed","file_counts":{"in_progress":0,"completed":1,"failed":0,"cancelled":0,"total":1}}""",
                headers = headersOf(HttpHeaders.ContentType, "application/json"))
        }
        OpenAI(OpenAIConfig(token = "test", engine = engine)).use { client ->
            client.createVectorStoreFileBatch(VectorStoreId("vs-1"), VectorStoreFileBatchRequest(
                files = listOf(VectorStoreFileCreateRequest(FileId("file-1"),
                    attributes = mapOf("reviewed" to JsonPrimitive(true)), chunkingStrategy = ChunkingStrategy())),
            ))
        }
    }

}
