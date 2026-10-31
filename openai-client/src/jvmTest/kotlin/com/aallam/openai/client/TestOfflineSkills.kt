package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.exception.AuthenticationException
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.exception.OpenAIException
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.skill.*
import com.aallam.openai.client.internal.api.SkillsApi
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

class TestOfflineSkills {
    @Test
    fun lifecyclePreservesDirectoryNamesZipUploadsVersionsAndBinaryDownloads() = runTest {
        val operations = mutableListOf<Pair<HttpMethod, String>>()
        val zip = byteArrayOf(80, 75, 0, -1)
        val options = RequestOptions(headers = mapOf("X-Test" to "skills"), urlParameters = mapOf("trace" to "yes"))
        val engine = MockEngine { request ->
            val path = request.url.encodedPath
            operations += request.method to path
            assertEquals("skills", request.headers["X-Test"])
            assertEquals("yes", request.url.parameters["trace"])
            when {
                request.method == HttpMethod.Post && path == "/v1/skills" -> {
                    assertTrue(request.body.contentType!!.match(ContentType.MultiPart.FormData))
                    val body = request.body.toByteArray().decodeToString()
                    val parts = multipartParts(body, request.body.contentType!!.parameter("boundary")!!)
                    assertEquals(listOf("files[]", "files[]"), parts.map { it.first.parameter("name") })
                    assertEquals(listOf("arithmetic/SKILL.md", "arithmetic/references/empty.txt"), parts.map { it.first.parameter("filename") })
                    assertEquals(listOf("manifest", ""), parts.map { it.second })
                    respond(skillJson, headers = jsonHeaders)
                }
                request.method == HttpMethod.Post && path.endsWith("/versions") -> {
                    val bytes = request.body.toByteArray()
                    val body = bytes.decodeToString()
                    val parts = multipartParts(body, request.body.contentType!!.parameter("boundary")!!)
                    assertEquals(listOf("files", "default"), parts.map { it.first.parameter("name") })
                    assertEquals("arithmetic.zip", parts[0].first.parameter("filename"))
                    assertContains(body, "Content-Type: application/zip")
                    assertTrue(bytes.asList().windowed(zip.size).contains(zip.asList()))
                    assertEquals("false", parts[1].second)
                    respond(versionJson, headers = jsonHeaders)
                }
                request.method == HttpMethod.Post -> {
                    assertEquals("/v1/skills/skill_1", path)
                    assertEquals(Json.parseToJsonElement("""{"default_version":"2"}"""), Json.parseToJsonElement(request.body.toByteArray().decodeToString()))
                    respond(skillJson.replace("\"default_version\":\"1\"", "\"default_version\":\"2\""), headers = jsonHeaders)
                }
                request.method == HttpMethod.Delete -> respond("""{"id":"deleted","object":"skill.deleted","deleted":true,"version":"1"}""", headers = jsonHeaders)
                path == "/v1/skills/skill_1/content" -> respond(zip, headers = headersOf(HttpHeaders.ContentType, "application/zip"))
                path == "/v1/skills/skill_1/versions/2/content" -> respond(byteArrayOf(), headers = headersOf(HttpHeaders.ContentType, "application/zip"))
                path == "/v1/skills" -> {
                    assertEquals("skill_0", request.url.parameters["after"])
                    assertEquals("2", request.url.parameters["limit"])
                    assertEquals("asc", request.url.parameters["order"])
                    respond(page(skillJson, "skill_1"), headers = jsonHeaders)
                }
                path == "/v1/skills/skill_1/versions" -> {
                    assertEquals("skillver_0", request.url.parameters["after"])
                    assertEquals("1", request.url.parameters["limit"])
                    assertEquals("desc", request.url.parameters["order"])
                    respond(page(versionJson, "skillver_2"), headers = jsonHeaders)
                }
                path == "/v1/skills/skill_1/versions/2" -> respond(versionJson, headers = jsonHeaders)
                path == "/v1/skills/skill_1" -> respond(skillJson, headers = jsonHeaders)
                else -> error("Unexpected route: $path")
            }
        }
        withClient(engine) { client ->
            val skill = client.createSkill(SkillUploadRequest(SkillFiles.Directory(listOf(
                FileSource("arithmetic/SKILL.md", Buffer().apply { write("manifest".encodeToByteArray()) }),
                FileSource("arithmetic/references/empty.txt", Buffer()),
            ))), options)
            assertEquals(id, skill.id)
            assertEquals(skill, client.retrieveSkill(id, options))
            val skills = client.skillsPage(SkillListQuery(SkillId("skill_0"), 2, SortOrder.Ascending), options)
            assertEquals("skill_1", skills.lastId)
            assertTrue(skills.hasMore!!)
            assertEquals(skill, skills.single())
            val version = client.createSkillVersion(id, SkillVersionUploadRequest(SkillFiles.Zip(FileSource("arithmetic.zip", Buffer().apply { write(zip) })), false), options)
            assertEquals("2", version.version)
            assertEquals(SkillVersionId("skillver_2"), version.id)
            assertEquals(version, client.retrieveSkillVersion(id, version.version, options))
            val versions = client.skillVersionsPage(id, SkillVersionListQuery(SkillVersionId("skillver_0"), 1, SortOrder.Descending), options)
            assertEquals("skillver_2", versions.lastId)
            assertTrue(versions.hasMore!!)
            assertEquals(version, versions.single())
            assertEquals("2", client.updateSkillDefaultVersion(id, SkillDefaultVersionRequest(version.version), options).defaultVersion)
            assertContentEquals(zip, client.downloadSkill(id, options))
            assertContentEquals(byteArrayOf(), client.downloadSkillVersion(id, version.version, options))
            assertTrue(client.deleteSkillVersion(id, "1", options))
            assertTrue(client.deleteSkill(id, options))
        }
        assertEquals(listOf(
            HttpMethod.Post to "/v1/skills", HttpMethod.Get to "/v1/skills/skill_1", HttpMethod.Get to "/v1/skills",
            HttpMethod.Post to "/v1/skills/skill_1/versions", HttpMethod.Get to "/v1/skills/skill_1/versions/2",
            HttpMethod.Get to "/v1/skills/skill_1/versions", HttpMethod.Post to "/v1/skills/skill_1",
            HttpMethod.Get to "/v1/skills/skill_1/content", HttpMethod.Get to "/v1/skills/skill_1/versions/2/content",
            HttpMethod.Delete to "/v1/skills/skill_1/versions/1", HttpMethod.Delete to "/v1/skills/skill_1",
        ), operations)
    }

    @Test
    fun missingResourcesAndEmptyPagesKeepTheirMeaningWhileOtherErrorsPropagate() = runTest {
        var status = HttpStatusCode.NotFound
        withClient(MockEngine { respond("""{"error":{"message":"missing","type":"invalid_request_error"}}""", status, jsonHeaders) }) { client ->
            assertNull(client.retrieveSkill(id))
            assertNull(client.retrieveSkillVersion(id, "2"))
            assertFalse(client.deleteSkill(id))
            assertFalse(client.deleteSkillVersion(id, "2"))
            assertFailsWith<InvalidRequestException> { client.downloadSkill(id) }
            assertFailsWith<InvalidRequestException> { client.downloadSkillVersion(id, "2") }
            status = HttpStatusCode.Unauthorized
            assertFailsWith<AuthenticationException> { client.retrieveSkill(id) }
            assertFailsWith<AuthenticationException> { client.deleteSkillVersion(id, "2") }
        }
        withClient(MockEngine { respond("""{"data":[],"first_id":null,"last_id":null,"has_more":false,"object":"list"}""", headers = jsonHeaders) }) { client ->
            val page = client.skillsPage()
            assertTrue(page.isEmpty())
            assertNull(page.lastId)
            assertFalse(page.hasMore!!)
        }
    }

    @Test
    fun customClientsMayOmitSkillOperations() = runTest {
        val client = object : Skills {}
        val files = SkillFiles.Zip(FileSource("fixture.zip", Buffer()))
        assertFailsWith<UnsupportedOperationException> { client.createSkill(SkillUploadRequest(files)) }
        assertFailsWith<UnsupportedOperationException> { client.retrieveSkill(id) }
        assertFailsWith<UnsupportedOperationException> { client.updateSkillDefaultVersion(id, SkillDefaultVersionRequest("1")) }
        assertFailsWith<UnsupportedOperationException> { client.skillsPage() }
        assertFailsWith<UnsupportedOperationException> { client.deleteSkill(id) }
        assertFailsWith<UnsupportedOperationException> { client.downloadSkill(id) }
        assertFailsWith<UnsupportedOperationException> { client.createSkillVersion(id, SkillVersionUploadRequest(files)) }
        assertFailsWith<UnsupportedOperationException> { client.retrieveSkillVersion(id, "1") }
        assertFailsWith<UnsupportedOperationException> { client.skillVersionsPage(id) }
        assertFailsWith<UnsupportedOperationException> { client.deleteSkillVersion(id, "1") }
        assertFailsWith<UnsupportedOperationException> { client.downloadSkillVersion(id, "1") }
    }

    @Test
    fun oneShotSkillAndVersionUploadsAreNotRetriedAfterConsumedOrUnconsumedFailures() = runTest {
        for (version in listOf(false, true)) for (consume in listOf(false, true)) {
            var attempts = 0
            val failure = IOException("upload failed")
            val source = TrackingSource()
            val files = SkillFiles.Zip(FileSource("fixture.zip", source))
            withClient(MockEngine { request ->
                attempts++
                if (consume) request.body.toByteArray()
                throw failure
            }, maxRetries = 2) { client ->
                val error = assertFailsWith<OpenAIException> {
                    if (version) client.createSkillVersion(id, SkillVersionUploadRequest(files)) else client.createSkill(SkillUploadRequest(files))
                }
                assertTrue(generateSequence<Throwable>(error) { it.cause }.any { it === failure })
            }
            assertEquals(1, attempts)
            assertEquals(1, source.closes)
        }
        var reads = 0
        withClient(MockEngine { if (++reads == 1) throw IOException("retry read") else respond(skillJson, headers = jsonHeaders) }, maxRetries = 2) { client ->
            assertEquals(id, client.retrieveSkill(id)?.id)
        }
        assertEquals(2, reads)
    }

    @Test
    fun consumedUnauthorizedSkillAndVersionUploadsAreNotReplayedByAuthentication() = runTest {
        for (version in listOf(false, true)) {
            var attempts = 0
            val first = TrackingSource()
            val second = TrackingSource()
            val files = SkillFiles.Directory(listOf(FileSource("fixture/SKILL.md", first), FileSource("fixture/data.txt", second)))
            withClient(MockEngine { request ->
                attempts++
                assertEquals("Bearer test", request.headers[HttpHeaders.Authorization])
                request.body.toByteArray()
                respond("""{"error":{"message":"Invalid API key","type":"invalid_request_error","code":"invalid_api_key"}}""",
                    HttpStatusCode.Unauthorized, headersOf(
                        HttpHeaders.ContentType to listOf("application/json"),
                        HttpHeaders.WWWAuthenticate to listOf("Bearer realm=\"openai\""),
                    ))
            }, maxRetries = 2) { client ->
                val error = assertFailsWith<AuthenticationException> {
                    if (version) client.createSkillVersion(id, SkillVersionUploadRequest(files)) else client.createSkill(SkillUploadRequest(files))
                }
                assertEquals("invalid_api_key", error.error.detail?.code)
            }
            assertEquals(1, attempts)
            assertEquals(1, first.closes)
            assertEquals(1, second.closes)
        }
    }

    @Test
    fun directoryCleanupAttemptsEverySourceAndPreservesThePrimaryFailure() = runTest {
        val failure = IOException("engine failed before consuming upload")
        val firstClose = IllegalStateException("first close failed")
        val secondClose = IllegalStateException("second close failed")
        val first = TrackingSource(firstClose)
        val second = TrackingSource(secondClose)
        val files = SkillFiles.Directory(listOf(FileSource("fixture/SKILL.md", first), FileSource("fixture/data.txt", second)))
        withClient(MockEngine { throw failure }) { client ->
            val error = assertFailsWith<OpenAIException> { client.createSkill(SkillUploadRequest(files)) }
            assertTrue(generateSequence<Throwable>(error) { it.cause }.any { it === failure })
            assertTrue(error.suppressed.any { it === firstClose })
            assertTrue(error.suppressed.any { it === secondClose })
        }
        assertEquals(1, first.closes)
        assertEquals(1, second.closes)
    }

    @Test
    fun cancellationClosesEveryUnconsumedDirectorySource() = runTest {
        val started = CompletableDeferred<Unit>()
        val first = TrackingSource()
        val second = TrackingSource()
        val files = SkillFiles.Directory(listOf(FileSource("fixture/SKILL.md", first), FileSource("fixture/data.txt", second)))
        withClient(MockEngine { started.complete(Unit); awaitCancellation() }) { client ->
            val job = launch { client.createSkill(SkillUploadRequest(files)) }
            started.await()
            job.cancelAndJoin()
            assertTrue(job.isCancelled)
        }
        assertEquals(1, first.closes)
        assertEquals(1, second.closes)
    }

    @Test
    fun invalidResourceSegmentsAreRejectedAndVersionUploadSourcesStillClose() = runTest {
        var requests = 0
        withClient(MockEngine { requests++; error("Unexpected request") }) { client ->
            for (value in listOf("", " ", ".", "..", "a/b", "a\\b", "a?b", "a#b", "a%b", "a\nb")) {
                assertFailsWith<IllegalArgumentException> { client.retrieveSkill(SkillId(value)) }
                assertFailsWith<IllegalArgumentException> { client.retrieveSkillVersion(id, value) }
            }
            val source = TrackingSource()
            assertFails { client.createSkillVersion(SkillId(".."), SkillVersionUploadRequest(SkillFiles.Zip(FileSource("fixture.zip", source)))) }
            assertEquals(1, source.closes)
        }
        assertEquals(0, requests)
    }

    private class TrackingSource(
        private val closeFailure: Throwable? = null,
        private val delegate: Buffer = Buffer().apply { write(byteArrayOf(80, 75, 0, -1)) },
    ) : RawSource by delegate {
        var closes = 0
        override fun close() { closes++; closeFailure?.let { throw it }; delegate.close() }
    }

    private fun multipartParts(body: String, boundary: String): List<Pair<ContentDisposition, String>> =
        body.split("--$boundary").mapNotNull { part ->
            val sections = part.split("\r\n\r\n", limit = 2)
            if (sections.size != 2) null else {
                val line = sections[0].lineSequence().first { it.startsWith("Content-Disposition:", ignoreCase = true) }
                ContentDisposition.parse(line.substringAfter(':').trim()) to sections[1].removeSuffix("\r\n")
            }
        }

    private suspend fun withClient(engine: MockEngine, maxRetries: Int = 0, block: suspend (Skills) -> Unit) {
        val transport = HttpTransport(createHttpClient(OpenAIConfig("test", engine = engine, retry = RetryStrategy(maxRetries = maxRetries, maxDelay = 1.milliseconds), logging = LoggingConfig(LogLevel.None))))
        try { block(SkillsApi(transport)) } finally { transport.close() }
    }

    private fun page(data: String, id: String) = """{"data":[$data],"first_id":"$id","last_id":"$id","has_more":true,"object":"list"}"""
    private val id = SkillId("skill_1")
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val skillJson = """{"id":"skill_1","created_at":123,"default_version":"1","latest_version":"2","name":"arithmetic","description":"Calculations"}"""
    private val versionJson = """{"id":"skillver_2","created_at":124,"skill_id":"skill_1","version":"2","name":"arithmetic","description":"Updated calculations"}"""
}
