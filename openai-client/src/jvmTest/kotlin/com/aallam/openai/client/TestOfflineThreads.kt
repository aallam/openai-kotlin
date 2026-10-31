package com.aallam.openai.client

import kotlin.test.Test
import kotlin.test.assertEquals

class TestOfflineThreads {

    @Test
    fun threads() = legacyTest(*threadLifecycle()) {
        val thread = openAI.thread()

        val retrieved = openAI.thread(
            thread.id,
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
        )
        assertEquals(metadata, updated.metadata)

        val deleted = openAI.delete(
            thread.id,
        )
        assertEquals(true, deleted)
    }
}
