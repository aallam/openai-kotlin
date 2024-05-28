package com.aallam.openai.client

import com.aallam.openai.api.chat.ChatCompletionChunk
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.client.internal.JsonLenient
import com.aallam.openai.client.internal.TestFileSystem
import com.aallam.openai.client.internal.testFilePath
import kotlin.test.Test
import okio.buffer

class TestChatCompletionChunk {
    @Test
    fun testContentFilterDeserialization() {
        val json = FileSource(path = testFilePath("json/azureContentFilterChunk.json"), fileSystem = TestFileSystem)
        val actualJson = json.source.buffer().readByteArray().decodeToString()
        JsonLenient.decodeFromString<ChatCompletionChunk>(actualJson)
    }

    @Test
    fun testDeserialization() {
        val json = FileSource(path = testFilePath("json/chatChunk.json"), fileSystem = TestFileSystem)
        val actualJson = json.source.buffer().readByteArray().decodeToString()
        JsonLenient.decodeFromString<ChatCompletionChunk>(actualJson)
    }
}
