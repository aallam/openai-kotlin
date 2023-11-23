package com.aallam.openai.client

import com.aallam.openai.api.core.Role
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.messageRequest
import kotlin.test.Test
import kotlin.test.assertEquals

class TestMessages : TestOpenAI() {

    @Test
    fun messages() = test {
        val thread = openAI.thread()
        val messageRequest = messageRequest {
            role = Role.User
            content = "How does AI work? Explain it in simple terms."
        }
        val message = openAI.message(threadId = thread.id, request = messageRequest)
        assertEquals(thread.id, message.threadId)
        assertEquals(messageRequest.role, message.role)
        assertEquals(messageRequest.content, (message.content.first() as MessageContent.Text).text.value)

        val retrieved = openAI.message(threadId = thread.id, messageId = message.id)
        assertEquals(message.id, retrieved.id)

        val metadata = mapOf("modified" to "true", "user" to "aallam")
        val modified = openAI.message(
            threadId = thread.id,
            messageId = message.id,
            metadata = metadata
        )
        assertEquals(metadata, modified.metadata)

        val messages = openAI.messages(threadId = thread.id)
        assertEquals(1, messages.size)

        val files = openAI.messageFiles(thread.id, message.id)
        assertEquals(0, files.size)

        openAI.delete(thread.id)
    }
}
