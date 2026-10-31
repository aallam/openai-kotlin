package com.aallam.openai.client

import com.aallam.openai.api.container.*
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.exception.AuthenticationException
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.exception.OpenAIException
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.internal.api.ContainersApi
import com.aallam.openai.client.internal.createHttpClient
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import kotlinx.coroutines.test.runTest
import kotlinx.coroutines.CompletableDeferred
import kotlinx.coroutines.awaitCancellation
import kotlinx.coroutines.cancelAndJoin
import kotlinx.coroutines.launch
import kotlinx.io.Buffer
import kotlinx.io.RawSource
import kotlinx.serialization.json.*
import java.io.IOException
import kotlin.test.*
import kotlin.time.Duration.Companion.milliseconds

class TestOfflineContainers {
    @Test
    fun lifecycleCoversAllRoutesBothFileCreationModesAndBinaryContent() = runTest {
        val binary = byteArrayOf(0, 1, -1, 127)
        val operations = mutableListOf<Pair<HttpMethod, String>>()
        val options = RequestOptions(headers = mapOf("X-Test" to "containers"), urlParameters = mapOf("trace" to "yes"))
        val engine = MockEngine { request ->
            val path = request.url.encodedPath
            operations += request.method to path
            assertEquals("containers", request.headers["X-Test"])
            assertEquals("yes", request.url.parameters["trace"])
            when {
                path == "/v1/containers" && request.method == HttpMethod.Post -> {
                    assertEquals(Json.parseToJsonElement("""{"name":"tools","expires_after":{"minutes":20,"anchor":"last_active_at"},"memory_limit":"1g","network_policy":{"type":"disabled"}}"""),
                        Json.parseToJsonElement(request.body.toByteArray().decodeToString()))
                    respond(containerJson, headers = jsonHeaders)
                }
                path == "/v1/containers" -> {
                    assertEquals(HttpMethod.Get, request.method)
                    assertEquals("cntr_0", request.url.parameters["after"])
                    assertEquals("2", request.url.parameters["limit"])
                    assertEquals("tools", request.url.parameters["name"])
                    assertEquals("asc", request.url.parameters["order"])
                    respond(page(containerJson, "cntr_1"), headers = jsonHeaders)
                }
                path == "/v1/containers/cntr_1/files" && request.method == HttpMethod.Post -> {
                    if (request.body.contentType!!.match(ContentType.MultiPart.FormData)) {
                        val bytes = request.body.toByteArray()
                        val text = bytes.decodeToString()
                        val disposition = text.lineSequence().first { it.startsWith("Content-Disposition:", ignoreCase = true) }
                        val parameters = ContentDisposition.parse(disposition.substringAfter(':').trim()).parameters
                        assertEquals("file", parameters.single { it.name == "name" }.value)
                        assertEquals("data.bin", parameters.single { it.name == "filename" }.value)
                        assertTrue(bytes.asList().windowed(binary.size).contains(binary.asList()))
                    } else {
                        assertEquals(ContentType.Application.Json, request.body.contentType!!.withoutParameters())
                        assertEquals(Json.parseToJsonElement("""{"file_id":"file_1"}"""), Json.parseToJsonElement(request.body.toByteArray().decodeToString()))
                    }
                    respond(fileJson, headers = jsonHeaders)
                }
                path == "/v1/containers/cntr_1/files" -> {
                    assertEquals(HttpMethod.Get, request.method)
                    assertEquals("cfile_0", request.url.parameters["after"])
                    assertEquals("1", request.url.parameters["limit"])
                    assertEquals("desc", request.url.parameters["order"])
                    respond(page(fileJson, "cfile_1"), headers = jsonHeaders)
                }
                path.endsWith("/content") -> {
                    assertEquals(HttpMethod.Get, request.method)
                    respond(binary, headers = headersOf(HttpHeaders.ContentType, "application/octet-stream"))
                }
                request.method == HttpMethod.Delete -> respond("""{"id":"deleted","object":"${if (path.contains("/files/")) "container.file.deleted" else "container.deleted"}","deleted":true}""", headers = jsonHeaders)
                path == "/v1/containers/cntr_1" -> respond(containerJson, headers = jsonHeaders)
                path == "/v1/containers/cntr_1/files/cfile_1" -> respond(fileJson, headers = jsonHeaders)
                else -> error("Unexpected route: $path")
            }
        }
        withClient(engine) { client ->
            val container = client.createContainer(ContainerRequest("tools", ContainerExpirationPolicy(20), memoryLimit = ContainerMemoryLimit.G1, networkPolicy = ContainerNetworkPolicy.Disabled), options)
            assertEquals(id, container.id)
            assertEquals(container, client.retrieveContainer(id, options))
            val containers = client.containersPage(ContainerListQuery(ContainerId("cntr_0"), 2, "tools", SortOrder.Ascending), options)
            assertEquals("cntr_1", containers.firstId)
            assertEquals("cntr_1", containers.lastId)
            assertTrue(containers.hasMore!!)
            assertEquals(container, containers.single())
            val uploaded = client.uploadContainerFile(id, FileSource("data.bin", Buffer().apply { write(binary) }), options)
            assertEquals(fileId, uploaded.id)
            assertEquals(uploaded, client.copyContainerFile(id, FileId("file_1"), options))
            assertEquals(uploaded, client.retrieveContainerFile(id, fileId, options))
            val files = client.containerFilesPage(id, ContainerFileListQuery(ContainerFileId("cfile_0"), 1, SortOrder.Descending), options)
            assertEquals("cfile_1", files.lastId)
            assertTrue(files.hasMore!!)
            assertEquals(uploaded, files.single())
            assertContentEquals(binary, client.downloadContainerFile(id, fileId, options))
            assertTrue(client.deleteContainerFile(id, fileId, options))
            assertTrue(client.deleteContainer(id, options))
        }
        assertEquals(listOf(
            HttpMethod.Post to "/v1/containers", HttpMethod.Get to "/v1/containers/cntr_1", HttpMethod.Get to "/v1/containers",
            HttpMethod.Post to "/v1/containers/cntr_1/files", HttpMethod.Post to "/v1/containers/cntr_1/files",
            HttpMethod.Get to "/v1/containers/cntr_1/files/cfile_1", HttpMethod.Get to "/v1/containers/cntr_1/files",
            HttpMethod.Get to "/v1/containers/cntr_1/files/cfile_1/content", HttpMethod.Delete to "/v1/containers/cntr_1/files/cfile_1",
            HttpMethod.Delete to "/v1/containers/cntr_1",
        ), operations)
    }

    @Test
    fun missingResourcesAreNullableOrFalseButOtherErrorsPropagate() = runTest {
        var status = HttpStatusCode.NotFound
        withClient(MockEngine { respond("""{"error":{"message":"missing","type":"invalid_request_error"}}""", status, jsonHeaders) }) { client ->
            assertNull(client.retrieveContainer(id))
            assertNull(client.retrieveContainerFile(id, fileId))
            assertFalse(client.deleteContainer(id))
            assertFalse(client.deleteContainerFile(id, fileId))
            assertFailsWith<InvalidRequestException> { client.downloadContainerFile(id, fileId) }
            status = HttpStatusCode.Unauthorized
            assertFailsWith<AuthenticationException> { client.retrieveContainer(id) }
            assertFailsWith<AuthenticationException> { client.retrieveContainerFile(id, fileId) }
            assertFailsWith<AuthenticationException> { client.deleteContainer(id) }
            assertFailsWith<AuthenticationException> { client.deleteContainerFile(id, fileId) }
        }
    }

    @Test
    fun customClientsCanOmitContainerOperations() = runTest {
        val client = object : Containers {}
        assertFailsWith<UnsupportedOperationException> { client.createContainer(ContainerRequest("test")) }
        assertFailsWith<UnsupportedOperationException> { client.retrieveContainer(id) }
        assertFailsWith<UnsupportedOperationException> { client.containersPage() }
        assertFailsWith<UnsupportedOperationException> { client.deleteContainer(id) }
        assertFailsWith<UnsupportedOperationException> { client.uploadContainerFile(id, FileSource("test", Buffer())) }
        assertFailsWith<UnsupportedOperationException> { client.copyContainerFile(id, FileId("file_1")) }
        assertFailsWith<UnsupportedOperationException> { client.retrieveContainerFile(id, fileId) }
        assertFailsWith<UnsupportedOperationException> { client.containerFilesPage(id) }
        assertFailsWith<UnsupportedOperationException> { client.deleteContainerFile(id, fileId) }
        assertFailsWith<UnsupportedOperationException> { client.downloadContainerFile(id, fileId) }
    }

    @Test
    fun oneShotUploadsAreNotRetriedWhileReadRequestsKeepConfiguredRetries() = runTest {
        for (consume in listOf(false, true)) {
            var attempts = 0
            val failure = IOException("upload failed")
            val source = TrackingSource()
            withClient(MockEngine { request ->
                attempts++
                if (consume) request.body.toByteArray()
                throw failure
            }, maxRetries = 2) { client ->
                val error = assertFailsWith<OpenAIException> { client.uploadContainerFile(id, FileSource("data.bin", source)) }
                assertTrue(generateSequence<Throwable>(error) { it.cause }.any { it === failure })
            }
            assertEquals(1, attempts)
            assertEquals(1, source.closes)
        }
        var reads = 0
        withClient(MockEngine { if (++reads == 1) throw IOException("retry read") else respond(containerJson, headers = jsonHeaders) }, maxRetries = 2) { client ->
            assertEquals(id, client.retrieveContainer(id)?.id)
        }
        assertEquals(2, reads)
    }

    @Test
    fun unconsumedUploadCleanupPreservesTheRequestFailure() = runTest {
        val failure = IOException("engine failed before consuming upload")
        val closeFailure = IllegalStateException("close failed")
        val source = TrackingSource(closeFailure = closeFailure)
        withClient(MockEngine { throw failure }) { client ->
            val error = assertFailsWith<OpenAIException> { client.uploadContainerFile(id, FileSource("data.bin", source)) }
            assertTrue(generateSequence<Throwable>(error) { it.cause }.any { it === failure })
            assertTrue(error.suppressed.any { it === closeFailure })
        }
        assertEquals(1, source.closes)
    }

    @Test
    fun cancellationClosesTheUnconsumedSource() = runTest {
        val started = CompletableDeferred<Unit>()
        val source = TrackingSource()
        withClient(MockEngine { started.complete(Unit); awaitCancellation() }) { client ->
            val job = launch { client.uploadContainerFile(id, FileSource("data.bin", source)) }
            started.await()
            job.cancelAndJoin()
            assertTrue(job.isCancelled)
        }
        assertEquals(1, source.closes)
    }

    @Test
    fun invalidResourceSegmentsAreRejectedAndUploadSourcesStillClose() = runTest {
        var requests = 0
        withClient(MockEngine { requests++; error("Unexpected request") }) { client ->
            for (value in listOf("", " ", ".", "..", "a/b", "a\\b", "a?b", "a#b", "a%b", "a\nb")) {
                assertFailsWith<IllegalArgumentException> { client.retrieveContainer(ContainerId(value)) }
                assertFailsWith<IllegalArgumentException> { client.retrieveContainerFile(id, ContainerFileId(value)) }
            }
            val source = TrackingSource()
            assertFails { client.uploadContainerFile(ContainerId(".."), FileSource("data.bin", source)) }
            assertEquals(1, source.closes)
        }
        assertEquals(0, requests)
    }

    private class TrackingSource(
        private val closeFailure: Throwable? = null,
        private val delegate: Buffer = Buffer().apply { write(byteArrayOf(0, 1, -1)) },
    ) : RawSource by delegate {
        var closes = 0
        override fun close() { closes++; closeFailure?.let { throw it }; delegate.close() }
    }

    private suspend fun withClient(engine: MockEngine, maxRetries: Int = 0, block: suspend (Containers) -> Unit) {
        val transport = HttpTransport(createHttpClient(OpenAIConfig("test", engine = engine, retry = RetryStrategy(maxRetries = maxRetries, maxDelay = 1.milliseconds), logging = LoggingConfig(LogLevel.None))))
        try { block(ContainersApi(transport)) } finally { transport.close() }
    }

    private fun page(data: String, id: String) = """{"data":[$data],"first_id":"$id","last_id":"$id","has_more":true,"object":"list"}"""
    private val id = ContainerId("cntr_1")
    private val fileId = ContainerFileId("cfile_1")
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val containerJson = """{"id":"cntr_1","created_at":123,"name":"tools","status":"running","expires_after":{"anchor":"last_active_at","minutes":20}}"""
    private val fileJson = """{"id":"cfile_1","bytes":4,"container_id":"cntr_1","created_at":124,"path":"/mnt/data/data.bin","source":"user"}"""
}
