package com.aallam.openai.client

import com.aallam.openai.api.assistant.CodeInterpreterResources
import com.aallam.openai.api.assistant.FileSearchResources
import com.aallam.openai.api.assistant.ToolResources
import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.thread.ThreadRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class TestThreads : TestOpenAI() {

    @Test
    fun threads() = test {
        val thread = openAI.thread()

        val retrieved = openAI.thread(thread.id)
        assertEquals(thread, retrieved)

        val metadata = mapOf("modified" to "true", "user" to "aallam")
        val updated = openAI.thread(id = thread.id, metadata = metadata)
        assertEquals(metadata, updated.metadata)

        val deleted = openAI.delete(thread.id)
        assertEquals(true, deleted)
    }

    @Test
    fun threadsV2() = test {
        val thread = openAI.thread(
            requestOptions = RequestOptions(
                betaVersion = 2
            )
        )

        val retrieved = openAI.thread(
            thread.id,
            requestOptions = RequestOptions(
                betaVersion = 2
            )
        )
        // thread creating request receives null tool_resource,
        // but retrieved one always has code interpreter with empty file_ids
        assertEquals(thread.id, retrieved?.id)
        assertEquals(thread.createdAt, retrieved?.createdAt)
        assertEquals(thread.metadata, retrieved?.metadata)

        val metadata = mapOf("modified" to "true", "user" to "aallam")
        val updated = openAI.thread(
            id = thread.id,
            metadata = metadata,
            requestOptions = RequestOptions(
                betaVersion = 2
            )
        )
        assertEquals(metadata, updated.metadata)

        val deleted = openAI.delete(
            thread.id,
            requestOptions = RequestOptions(
                betaVersion = 2
            )
        )
        assertEquals(true, deleted)
    }
}
