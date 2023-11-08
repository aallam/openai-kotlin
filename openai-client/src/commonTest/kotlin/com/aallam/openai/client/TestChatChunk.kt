package com.aallam.openai.client

import com.aallam.openai.api.chat.ChatChunk
import com.aallam.openai.api.chat.ChatDelta
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.core.FinishReason
import com.aallam.openai.client.extension.mergeToChatMessage
import kotlin.test.Test
import kotlin.test.assertEquals

class TestChatChunk {

    @Test
    fun testMerge() {
        val chunks = listOf(
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = ChatRole(role = "assistant"),
                    content = ""
                ),
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = "The"
                ),
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = " World"
                ),
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = " Series"
                ),
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = " in"
                ),
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = " "
                ),
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = "202"
                ),
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = "0"
                ),
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = " is"
                ),
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = " being held"
                ),
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = " in"
                ),
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = " Texas"
                ),
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = "."
                ),
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = null
                ),
                finishReason = FinishReason(value = "stop")
            )
        )
        val chatMessage = chunks.mergeToChatMessage()
        val message = ChatMessage(
            role = ChatRole.Assistant,
            content = "The World Series in 2020 is being held in Texas.",
            name = null,
        )
        assertEquals(chatMessage, message)
    }
}
