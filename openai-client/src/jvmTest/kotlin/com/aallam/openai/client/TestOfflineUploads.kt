package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.upload.*
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.io.Buffer
import kotlinx.serialization.json.*
import kotlin.test.*

class TestOfflineUploads {
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val id = UploadId("upload_1")
    private val pending = """{"id":"upload_1","bytes":4,"created_at":10,"expires_at":3610,"filename":"data.bin","purpose":"batch","status":"pending"}"""
    private val completed = """{"id":"upload_1","bytes":4,"created_at":10,"expires_at":3610,"filename":"data.bin","purpose":"batch","status":"completed","file":{"id":"file_1","bytes":null,"created_at":12,"filename":"data.bin","purpose":"batch","expires_at":null,"status_details":null,"status":"processed"}}"""

    @Test
    fun multipartLifecyclePreservesPartDataOrderAndResultingFile() = runTest {
        val binary = byteArrayOf(0, 1, -1, 127)
        val routes = mutableListOf<String>()
        val options = RequestOptions(headers = mapOf("X-Test" to "upload"), urlParameters = mapOf("trace" to "yes"))
        val engine = MockEngine { request ->
            routes += request.url.encodedPath
            assertEquals("upload", request.headers["X-Test"])
            assertEquals("yes", request.url.parameters["trace"])
            when (request.url.encodedPath) {
                "/v1/uploads" -> {
                    assertEquals(HttpMethod.Post, request.method)
                    val body = Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
                    assertEquals("application/octet-stream", body["mime_type"]!!.jsonPrimitive.content)
                    assertEquals("batch", body["purpose"]!!.jsonPrimitive.content)
                    assertEquals("3600", body["expires_after"]!!.jsonObject["seconds"]!!.jsonPrimitive.content)
                    assertEquals("created_at", body["expires_after"]!!.jsonObject["anchor"]!!.jsonPrimitive.content)
                    respond(pending, headers = jsonHeaders)
                }
                "/v1/uploads/upload_1/parts" -> {
                    assertEquals(HttpMethod.Post, request.method)
                    assertTrue(request.body.contentType!!.match(ContentType.MultiPart.FormData))
                    val bytes = request.body.toByteArray()
                    val text = bytes.decodeToString()
                    assertTrue(text.contains("name=data") || text.contains("name=\"data\""))
                    assertTrue(text.contains("filename=chunk.bin") || text.contains("filename=\"chunk.bin\""))
                    assertTrue(bytes.asList().windowed(binary.size).contains(binary.asList()))
                    respond("""{"id":"part_2","created_at":11,"upload_id":"upload_1","object":"upload.part"}""", headers = jsonHeaders)
                }
                "/v1/uploads/upload_1/complete" -> {
                    assertEquals(HttpMethod.Post, request.method)
                    val body = Json.parseToJsonElement(request.body.toByteArray().decodeToString()).jsonObject
                    assertEquals(Json.parseToJsonElement("""["part_2","part_1"]"""), body["part_ids"])
                    assertEquals("digest", body["md5"]!!.jsonPrimitive.content)
                    respond(completed, headers = jsonHeaders)
                }
                "/v1/files/file_1/content" -> respond(binary)
                else -> error("Unexpected route ${request.url}")
            }
        }
        OpenAI(OpenAIConfig("test", engine = engine, logging = LoggingConfig(LogLevel.None))).use { client ->
            val upload = client.upload(UploadRequest(4, "data.bin", "application/octet-stream", Purpose("batch"), UploadExpirationPolicy(3600)), options)
            assertEquals(UploadStatus.Pending, upload.status)
            assertNull(upload.file)
            val part = client.uploadPart(upload.id, FileSource("chunk.bin", Buffer().apply { write(binary) }), options)
            assertEquals(id, part.uploadId)
            val result = client.completeUpload(id, UploadCompletionRequest(listOf(part.id, UploadPartId("part_1")), "digest"), options)
            assertEquals(UploadStatus.Completed, result.status)
            assertNull(result.file!!.bytes)
            assertContentEquals(binary, client.download(result.file!!.id, options))
        }
        assertEquals(listOf("/v1/uploads", "/v1/uploads/upload_1/parts", "/v1/uploads/upload_1/complete", "/v1/files/file_1/content"), routes)
    }

    @Test
    fun cancellationAndLifecycleFailuresPropagateWithoutPretendingSuccess() = runTest {
        var cancelled = false
        val engine = MockEngine { request ->
            assertEquals(HttpMethod.Post, request.method)
            assertEquals("keep", request.headers["X-Test"])
            when (request.url.encodedPath) {
                "/v1/uploads/upload_1/cancel" -> {
                    cancelled = true
                    respond(pending.replace("pending", "cancelled"), headers = jsonHeaders)
                }
                else -> {
                    assertTrue(cancelled)
                    respond("""{"error":{"message":"Upload is cancelled","type":"invalid_request_error"}}""", HttpStatusCode.BadRequest, jsonHeaders)
                }
            }
        }
        OpenAI(OpenAIConfig("test", engine = engine, retry = RetryStrategy(maxRetries = 0), logging = LoggingConfig(LogLevel.None))).use { client ->
            val options = RequestOptions(headers = mapOf("X-Test" to "keep"))
            assertEquals(UploadStatus.Cancelled, client.cancelUpload(id, options).status)
            assertFailsWith<InvalidRequestException> { client.completeUpload(id, UploadCompletionRequest(emptyList()), options) }
        }
    }

    @Test
    fun customClientsCanOmitUploadsCapability() = runTest {
        val custom = object : Uploads {}
        assertFailsWith<UnsupportedOperationException> { custom.upload(UploadRequest(0, "empty", "text/plain", Purpose("batch"))) }
        assertFailsWith<UnsupportedOperationException> { custom.uploadPart(id, FileSource("part", Buffer())) }
        assertFailsWith<UnsupportedOperationException> { custom.completeUpload(id, UploadCompletionRequest(emptyList())) }
        assertFailsWith<UnsupportedOperationException> { custom.cancelUpload(id) }
    }
}
