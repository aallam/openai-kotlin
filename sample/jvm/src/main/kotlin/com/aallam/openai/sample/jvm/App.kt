package com.aallam.openai.sample.jvm

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.engine.Engine
import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.api.search.SearchRequest
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.runBlocking

fun main() {
    runBlocking {

        val token = System.getenv("OPENAI_API_KEY")
        val openAI = OpenAI(token)

        println("\nGetting available engines...")
        openAI.engines().forEach { it.id }

        println("\nGetting ada engine...")
        val ada: Engine = openAI.engine(EngineId.Ada)
        println(ada)

        println("\nCreating completion...")
        val completionRequest = CompletionRequest(
            prompt = "Somebody once told me the world is gonna roll me",
            echo = true
        )
        openAI.createCompletion(EngineId.Ada, completionRequest).choices.forEach {
            println("${it.index}: ${it.text}")
        }

        println("\nSearching documents...")
        val searchRequest = SearchRequest(
            documents = listOf("Water", "Earth", "Electricity", "Fire"),
            query = "Pikachu"
        )
        openAI.search(EngineId.Ada, searchRequest).forEach {
            println("${it.document} #${it.score}")
        }
    }
}
