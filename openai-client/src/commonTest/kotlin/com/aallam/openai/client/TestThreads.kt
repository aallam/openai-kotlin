package com.aallam.openai.client

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
}
