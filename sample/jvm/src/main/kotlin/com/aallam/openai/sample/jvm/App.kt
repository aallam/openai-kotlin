package com.aallam.openai.sample.jvm

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.engine.Ada
import com.aallam.openai.api.engine.Engine
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val apiKey = System.getenv("OPENAI_API_KEY")
    val token = requireNotNull(apiKey) { "OPENAI_API_KEY environment variable must be set." }
    val openAI = OpenAI(token)

    println("> Getting available engines...")
    openAI.engines().forEach(::println)

    println("\n> Getting ada engine...")
    val ada: Engine = openAI.engine(Ada)
    println(ada)

    println("\n>️ Creating completion...")
    val completionRequest = CompletionRequest(prompt = "Somebody once told me the world is gonna roll me")
    openAI.completion(Ada, completionRequest).choices.forEach(::println)

    println("\n>️ Creating completion stream...")
    openAI.completions(Ada, completionRequest)
        .onEach { print(it.choices[0].text) }
        .onCompletion { println() }
        .launchIn(this)
        .join()

    println("\n> Read files...")
    val files = openAI.files()
    println(files)
}
