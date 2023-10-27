package com.aallam.openai.sample.jvm

import com.aallam.ktoken.Tokenizer
import com.aallam.openai.api.chat.ChatCompletionRequest
import com.aallam.openai.api.chat.ChatMessage
import com.aallam.openai.api.chat.ChatRole
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI

suspend fun tokensCount(openAI: OpenAI) {
    val messages = listOf(
        ChatMessage(
            role = ChatRole.System,
            content = "You are a helpful, pattern-following assistant that translates corporate jargon into plain English.",
        ),
        ChatMessage(
            role = ChatRole.System,
            name = "example_user",
            content = "New synergies will help drive top-line growth.",
        ),
        ChatMessage(
            role = ChatRole.System,
            name = "example_assistant",
            content = "Things working well together will increase revenue.",
        ),
        ChatMessage(
            role = ChatRole.System,
            name = "example_user",
            content = "Let's circle back when we have more bandwidth to touch base on opportunities for increased leverage.",
        ),
        ChatMessage(
            role = ChatRole.System,
            name = "example_assistant",
            content = "Let's talk later when we're less busy about how to do better.",
        ),
        ChatMessage(
            role = ChatRole.User,
            content = "This late pivot means we don't have time to boil the ocean for the client deliverable.",
        ),
    )

    val models = listOf(
        "gpt-3.5-turbo-0301", "gpt-3.5-turbo-0613", "gpt-3.5-turbo", "gpt-4-0314", "gpt-4-0613", "gpt-4",
    )
    for (model in models) {
        println(model)
        val tokens = encoding(messages, model)
        println("$tokens prompt tokens counted by Ktoken.")
        val request = ChatCompletionRequest(
            model = ModelId(model),
            messages = messages,
            temperature = 0.0,
            maxTokens = 1,
        )
        val completion = openAI.chatCompletion(request)
        println("${completion.usage?.promptTokens} prompt tokens counted by the OpenAI API\n")
    }
}

suspend fun encoding(messages: List<ChatMessage>, model: String): Int {
    val (tokensPerMessage, tokensPerName) = when (model) {
        "gpt-3.5-turbo-0613", "gpt-3.5-turbo-16k-0613", "gpt-4-0314", "gpt-4-32k-0314", "gpt-4-0613", "gpt-4-32k-0613" -> 3 to 1
        "gpt-3.5-turbo-0301" -> 4 to -1 // every message follows <|start|>{role/name}\n{content}<|end|>\n
        "gpt-3.5-turbo" -> {
            println("Warning: gpt-3.5-turbo may update over time. Returning num tokens assuming gpt-3.5-turbo-0613.")
            return encoding(messages, "gpt-3.5-turbo-0613")
        }

        "gpt-4" -> {
            println("Warning: gpt-4 may update over time. Returning num tokens assuming gpt-4-0613.")
            return encoding(messages, "gpt-4-0613")
        }

        else -> error("unsupported model")
    }

    val tokenizer = Tokenizer.of(model)
    var numTokens = 0
    for (message in messages) {
        numTokens += tokensPerMessage
        message.run {
            numTokens += tokenizer.encode(role.role).size
            name?.let { numTokens += tokensPerName + tokenizer.encode(it).size }
            content?.let { numTokens += tokenizer.encode(it).size }
        }
    }
    numTokens += 3 // every reply is primed with <|start|>assistant<|message|>
    return numTokens
}
