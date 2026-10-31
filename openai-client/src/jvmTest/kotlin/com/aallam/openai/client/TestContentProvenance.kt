package com.aallam.openai.client

import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.provenance.ProvenanceResult
import com.aallam.openai.client.internal.testFilePath
import kotlinx.coroutines.test.runTest
import kotlin.test.*
import kotlin.time.Duration.Companion.minutes

class TestContentProvenance {
    @Test
    fun imageReturnsBothApplicableSignalResults() = runTest(timeout = 1.minutes) {
        val client = OpenAI(OpenAIConfig(token = token, logging = LoggingConfig(LogLevel.None), retry = RetryStrategy(maxRetries = 0)))
        try {
            val check = client.checkContentProvenance(FileSource(testFilePath("image/pets.png")), "image/png")
            assertEquals("content_provenance_check", check.objectType)
            assertTrue(check.createdAt > 0)
            assertTrue(check.results.any { it is ProvenanceResult.C2pa })
            assertTrue(check.results.any { it is ProvenanceResult.SynthId })
            assertTrue(check.results.all { it.outcome in setOf("detected", "not_detected") })
        } finally {
            client.close()
        }
    }
}
