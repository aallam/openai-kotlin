package com.aallam.openai.client

import com.aallam.openai.api.file.FileRequest
import com.aallam.openai.api.file.Purpose
import com.aallam.openai.client.internal.waitFileProcess
import kotlinx.coroutines.test.runTest
import okio.Path
import okio.Path.Companion.toPath
import ulid.ULID
import kotlin.test.*

class TestFiles : TestOpenAI() {

    @Test
    fun file() {
        runTest {
            val id = ULID.randomULID()
            val filePath: Path = "$id.jsonl".toPath()
            val jsonl = """
                    { "text": "AJ" }
                    { "text": "Abby" }
                    { "text": "Abe" }
                    { "text": "Ace" }
                """.trimIndent()
            fileSystem.write(filePath) { writeUtf8(jsonl) }

            val request = FileRequest(
                file = filePath.toString(),
                purpose = Purpose("answers")
            )
            val filename = filePath.name

            // Create file
            val fileCreate = openAI.file(request)
            assertEquals(filename, fileCreate.filename)

            // Get created file
            openAI.waitFileProcess(fileCreate.id)

            // Delete file
            openAI.delete(fileCreate.id)

            // Check deleted file
            val fileGetAfterDelete = openAI.file(fileCreate.id)
            assertNull(fileGetAfterDelete)
        }
    }

    @Test
    fun files() {
        runTest {
            val response = openAI.files()
            assertNotNull(response)
            assertTrue(response.isNotEmpty())
        }
    }
}