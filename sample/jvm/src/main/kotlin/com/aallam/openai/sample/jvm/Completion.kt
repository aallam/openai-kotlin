package com.aallam.openai.sample.jvm

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach

suspend fun CoroutineScope.completion(openAI: OpenAI) {
    println("\n>️ Creating completion...")
    val completionRequest = CompletionRequest(
        model = ModelId("text-ada-001"),
        prompt = "Somebody once told me the world is gonna roll me"
    )
    openAI.completion(completionRequest).choices.forEach(::println)

    println("\n>️ Creating completion stream...")
    openAI.completions(completionRequest)
        .onEach { print(it.choices[0].text) }
        .onCompletion { println() }
        .launchIn(this)
        .join()
}

suspend fun completionHeaders(openAI: OpenAI) {
    println("\n>️ Creating completion...")
    val completionRequest = CompletionRequest(
        model = ModelId("text-ada-001"),
        prompt = "Somebody once told me the world is gonna roll me"
    )

    val result = openAI.completionHeaders(completionRequest)
    val text = result.first.choices.joinToString("") { it.text }
    val headers = result.second
    println("Completion result: $text. Headers: $headers")
}
