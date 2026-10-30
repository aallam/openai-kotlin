package com.aallam.openai.api.misc

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.batch.*
import com.aallam.openai.api.core.Endpoint
import com.aallam.openai.api.file.*
import com.aallam.openai.api.vectorstore.*
import kotlinx.serialization.json.*
import kotlin.test.*

@OptIn(BetaOpenAI::class)
class TestRetrieval {
    @Test
    fun updateDistinguishesOmittedFromExplicitNull() {
        val omitted = Json.encodeToJsonElement(VectorStoreUpdateRequest.serializer(), VectorStoreUpdateRequest()).jsonObject
        assertTrue(omitted.isEmpty())
        val cleared = Json.encodeToJsonElement(VectorStoreUpdateRequest.serializer(), VectorStoreUpdateRequest(clearExpiresAfter = true)).jsonObject
        assertEquals(buildJsonObject { put("expires_after", JsonNull) }, cleared)
        assertTrue(Json.decodeFromJsonElement(VectorStoreUpdateRequest.serializer(), cleared).clearExpiresAfter)
        assertFailsWith<IllegalArgumentException> { VectorStoreUpdateRequest(name = "name", clearName = true) }
    }

    @Test
    fun batchAndFileConfigurationsPreserveWireFields() {
        val request = batchCreateRequest {
            inputFileId = FileId("file-input")
            endpoint = Endpoint.Responses
            completionWindow = CompletionWindow.TwentyFourHours
            outputExpiresAfter = FileExpiration(3600)
        }
        val encoded = Json.encodeToJsonElement(BatchCreateRequest.serializer(), request).jsonObject
        assertEquals("/v1/responses", encoded["endpoint"]?.jsonPrimitive?.content)
        // The default anchor must still be sent when the API requires it.
        val policy = encoded["output_expires_after"]!!.jsonObject
        assertEquals("created_at", policy["anchor"]?.jsonPrimitive?.content)
        assertEquals(3600, policy["seconds"]?.jsonPrimitive?.int)
        val perFile = VectorStoreFileCreateRequest(FileId("file-1"), mapOf("reviewed" to JsonPrimitive(true)), ChunkingStrategy("static", StaticChunking(800, 200)))
        val batch = Json.encodeToJsonElement(VectorStoreFileBatchRequest.serializer(), VectorStoreFileBatchRequest(files = listOf(perFile))).jsonObject
        assertFalse("file_ids" in batch)
        assertEquals(true, batch["files"]!!.jsonArray.single().jsonObject["attributes"]!!.jsonObject["reviewed"]!!.jsonPrimitive.boolean)
    }

    @Test
    fun searchPreservesFiltersAndResultMetadata() {
        val request = VectorStoreSearchRequest(listOf("pricing"), VectorStoreFilter("eq", "language", JsonPrimitive("en")), rankingOptions = VectorStoreRankingOptions(scoreThreshold = 0.5))
        val body = Json.encodeToJsonElement(VectorStoreSearchRequest.serializer(), request).jsonObject
        assertEquals("en", body["filters"]!!.jsonObject["value"]!!.jsonPrimitive.content)
        val result = Json.decodeFromString(VectorStoreSearchPage.serializer(), """{
            "data":[{"file_id":"file-1","filename":"pricing.txt","score":0.8,"attributes":{"reviewed":true},"content":[{"type":"text","text":"Pricing"}]}],
            "has_more":false,"next_page":null,"search_query":["pricing"]
        }""")
        assertEquals("Pricing", result.data.single().content.single().text)
        assertEquals(true, result.data.single().attributes?.get("reviewed")?.boolean)
        assertEquals(listOf("pricing"), result.searchQuery)
    }
    @Test
    fun fileBatchRejectsAmbiguousOrIgnoredConfiguration() {
        val ids = listOf(FileId("file-1"))
        val files = listOf(VectorStoreFileCreateRequest(FileId("file-1")))
        assertFailsWith<IllegalArgumentException> { VectorStoreFileBatchRequest() }
        assertFailsWith<IllegalArgumentException> { VectorStoreFileBatchRequest(fileIds = ids, files = files) }
        assertFailsWith<IllegalArgumentException> {
            VectorStoreFileBatchRequest(files = files, attributes = mapOf("kind" to JsonPrimitive("docs")))
        }
        assertFailsWith<IllegalArgumentException> {
            VectorStoreFileBatchRequest(files = files, chunkingStrategy = ChunkingStrategy())
        }
        assertFailsWith<IllegalArgumentException> { VectorStoreFileBatchRequest(fileIds = emptyList()) }
        assertFailsWith<IllegalArgumentException> { VectorStoreFileBatchRequest(fileIds = List(2001) { FileId("file-$it") }) }
        assertEquals(2000, VectorStoreFileBatchRequest(fileIds = List(2000) { FileId("file-$it") }).fileIds?.size)
        assertFailsWith<IllegalArgumentException> {
            VectorStoreFileBatchRequest(files = listOf(VectorStoreFileCreateRequest()))
        }
    }

}
