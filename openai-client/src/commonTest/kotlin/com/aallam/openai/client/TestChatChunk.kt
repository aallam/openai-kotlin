package com.aallam.openai.client

import com.aallam.openai.api.chat.ChatChunk
import com.aallam.openai.api.chat.ChatDelta
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.chat.ContentFilterOffsets
import com.aallam.openai.api.chat.ContentFilterResult
import com.aallam.openai.api.chat.ContentFilterResults
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
                contentFilterOffsets = null,
                contentFilterResults = null,
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = "The"
                ),
                contentFilterOffsets = null,
                contentFilterResults = null,
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = " World"
                ),
                contentFilterOffsets = null,
                contentFilterResults = null,
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = " Series"
                ),
                contentFilterOffsets = null,
                contentFilterResults = null,
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = " in"
                ),
                contentFilterOffsets = null,
                contentFilterResults = null,
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = " "
                ),
                contentFilterOffsets = null,
                contentFilterResults = null,
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = "202"
                ),
                contentFilterOffsets = null,
                contentFilterResults = null,
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = "0"
                ),
                contentFilterOffsets = null,
                contentFilterResults = null,
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = " is"
                ),
                contentFilterOffsets = null,
                contentFilterResults = null,
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = " being held"
                ),
                contentFilterOffsets = null,
                contentFilterResults = null,
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = " in"
                ),
                contentFilterOffsets = null,
                contentFilterResults = null,
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = " Texas"
                ),
                contentFilterOffsets = null,
                contentFilterResults = null,
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = "."
                ),
                contentFilterOffsets = null,
                contentFilterResults = null,
                finishReason = null
            ),
            ChatChunk(
                index = 0,
                delta = ChatDelta(
                    role = null,
                    content = null
                ),
                contentFilterOffsets = null,
                contentFilterResults = null,
                finishReason = FinishReason(value = "stop")
            ),
            ChatChunk(
                index = 0,
                delta = null,
                contentFilterOffsets = ContentFilterOffsets(
                    checkOffset = 1,
                    startOffset = 1,
                    endOffset = 1,
                ),
                contentFilterResults = ContentFilterResults(
                    hate = ContentFilterResult(
                        filtered = false,
                        severity = "high",
                    )
                ),
                finishReason = FinishReason(value = "stop")
            )
        )
        val chatMessage = chunks.mergeToChatMessage()
        val message = ChatMessage(
            role = ChatRole.Assistant,
            content = "The World Series in 2020 is being held in Texas.",
            name = null,
            contentFilterResults = listOf(
                ContentFilterResults(
                    hate = ContentFilterResult(
                        filtered = false,
                        severity = "high",
                    )
                )
            ),
            contentFilterOffsets = listOf(
                ContentFilterOffsets(
                    checkOffset = 1,
                    startOffset = 1,
                    endOffset = 1,
                )
            ),
        )
        assertEquals(chatMessage, message)
    }
}
