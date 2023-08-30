package com.aallam.openai.sample.jvm

import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

suspend fun chat(openAI: OpenAI) {
    println("\n> Create chat completions...")
    val chatCompletionRequest = ChatCompletionRequest(
        model = ModelId("gpt-3.5-turbo"),
        messages = listOf(
            ChatMessage(
                role = ChatRole.System,
                content = "You are a helpful assistant that translates English to French."
            ),
            ChatMessage(
                role = ChatRole.User,
                content = "Translate the following English text to French: “OpenAI is awesome!”"
            )
        )
    )
    openAI.chatCompletion(chatCompletionRequest).choices.forEach(::println)

    println("\n>️ Creating chat completions stream...")
    openAI.chatCompletions(chatCompletionRequest)
        .onEach { print(it.choices.first().delta.content.orEmpty()) }
        .onCompletion { println() }
        .collect()
}
