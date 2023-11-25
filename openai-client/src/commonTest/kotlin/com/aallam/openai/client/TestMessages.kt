package com.aallam.openai.client

import com.aallam.openai.api.core.PaginatedList
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.messageRequest
import com.aallam.openai.client.internal.JsonLenient
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


    @Test
    fun json() = test {
        val json = """
            {
              "object": "list",
              "data": [
                {
                  "id": "msg_lZOOPLOnNpCUTS9X2ZIXeb4d",
                  "object": "thread.message",
                  "created_at": 1700940931,
                  "thread_id": "thread_YtDrtL7leLexy5tK92Vs6EwB",
                  "role": "assistant",
                  "content": [
                    {
                      "type": "text",
                      "text": {
                        "value": "The right to freedom of opinion and expression as stated in The Universal Declaration of Human Rights is articulated in Article 19. This article establishes that \"Everyone has the right to freedom of opinion and expression,\" which includes the freedom to hold opinions without interference and to seek, receive, and impart information and ideas through any media and regardless of frontiers【9†source】.",
                        "annotations": [
                          {
                            "type": "file_citation",
                            "text": "【9†source】",
                            "start_index": 390,
                            "end_index": 400,
                            "file_citation": {
                              "file_id": "file-RKOMlETO56zetEabhgAHY7Pn",
                              "quote": "Article 19 \n\n\nEveryone has the right to freedom of opinion and expression; this right includes\nfreedom to hold opinions without interference and to seek receive and impart\ninformation and ideas through any media and regardless of frontiers"
                            }
                          }
                        ]
                      }
                    }
                  ],
                  "file_ids": [],
                  "assistant_id": "asst_moDx00IB4pdymebVXgdZUDsw",
                  "run_id": "run_P9qOWCBojHLLo6a8y67HhvkB",
                  "metadata": {}
                },
                {
                  "id": "msg_QEQm77djXUIi6tsaYY5LfmZr",
                  "object": "thread.message",
                  "created_at": 1700940926,
                  "thread_id": "thread_YtDrtL7leLexy5tK92Vs6EwB",
                  "role": "user",
                  "content": [
                    {
                      "type": "text",
                      "text": {
                        "value": "Can you explain the right to freedom of opinion and expression as stated in The Universal Declaration of Human Rights?",
                        "annotations": []
                      }
                    }
                  ],
                  "file_ids": [],
                  "assistant_id": null,
                  "run_id": null,
                  "metadata": {}
                }
              ],
              "first_id": "msg_lZOOPLOnNpCUTS9X2ZIXeb4d",
              "last_id": "msg_QEQm77djXUIi6tsaYY5LfmZr",
              "has_more": false
            }
        """.trimIndent()
        val decoded = JsonLenient.decodeFromString<PaginatedList<Message>>(json)
        println(decoded)
    }
}
