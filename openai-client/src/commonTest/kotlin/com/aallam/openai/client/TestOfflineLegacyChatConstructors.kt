@file:OptIn(com.aallam.openai.api.BetaOpenAI::class)

package com.aallam.openai.client

import com.aallam.openai.api.chat.*
import com.aallam.openai.api.core.Usage
import com.aallam.openai.api.model.ModelId
import kotlin.test.Test
import kotlin.test.assertEquals

/** Consumer-side inferred references preserve constructor and generated copy arities. */
class TestOfflineLegacyChatConstructors {
    @Test
    fun inferredConstructorAndCopyReferencesKeepTheirOriginalShapes() {
        val chatCompletionConstructor = ::ChatCompletion
        val chatCompletion = chatCompletionConstructor("example", 0L, ModelId("model"), emptyList(), null, null)
        val chatCompletionCopy = chatCompletion::copy
        assertEquals(chatCompletion, chatCompletionCopy("example", 0L, ModelId("model"), emptyList(), null, null))

        val chatCompletionChunkConstructor = ::ChatCompletionChunk
        val chatCompletionChunk = chatCompletionChunkConstructor("example", 0L, ModelId("model"), emptyList(), null, null)
        val chatCompletionChunkCopy = chatCompletionChunk::copy
        assertEquals(chatCompletionChunk, chatCompletionChunkCopy("example", 0L, ModelId("model"), emptyList(), null, null))

        val chatCompletionRequestConstructor = ::ChatCompletionRequest
        val chatCompletionRequest = chatCompletionRequestConstructor(ModelId("model"), emptyList(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null)
        val chatCompletionRequestCopy = chatCompletionRequest::copy
        assertEquals(chatCompletionRequest, chatCompletionRequestCopy(ModelId("model"), emptyList(), null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null, null))

        val chatDeltaConstructor = ::ChatDelta
        val chatDelta = chatDeltaConstructor(null, null, null, null, null, null)
        val chatDeltaCopy = chatDelta::copy
        assertEquals(chatDelta, chatDeltaCopy(null, null, null, null, null, null))

        val chatAnnotationConstructor = ::ChatAnnotation
        val chatAnnotation = chatAnnotationConstructor("example", null, null, null, null, null)
        val chatAnnotationCopy = chatAnnotation::copy
        assertEquals(chatAnnotation, chatAnnotationCopy("example", null, null, null, null, null))

        val streamOptionsConstructor = ::StreamOptions
        val streamOptions = streamOptionsConstructor(null)
        val streamOptionsCopy = streamOptions::copy
        assertEquals(streamOptions, streamOptionsCopy(null))

        val functionToolConstructor = ::FunctionTool
        val functionTool = functionToolConstructor("example", null, null)
        val functionToolCopy = functionTool::copy
        assertEquals(functionTool, functionToolCopy("example", null, null))

        val usageConstructor = ::Usage
        val usage = usageConstructor(null, null, null)
        val usageCopy = usage::copy
        assertEquals(usage, usageCopy(null, null, null))

        val textConstructor = ::TextPart
        val text = textConstructor("hello")
        val textCopy = text::copy
        assertEquals(text, textCopy("hello"))
        val image = ImagePart("https://example.com/image.png")
        val imageCopy = image::copy
        assertEquals(image, imageCopy(ImagePart.ImageURL("https://example.com/image.png")))
        val message = ChatMessage.User("hello")
        val messageCopy = message::copy
        assertEquals(message, messageCopy(ChatRole.User, TextContent("hello"), null, null, null, null, null, null, null, null))
    }
}
