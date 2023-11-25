package com.aallam.openai.sample.jvm

import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val apiKey = System.getenv("OPENAI_API_KEY")
    val token = requireNotNull(apiKey) { "OPENAI_API_KEY environment variable must be set." }
    val openAI = OpenAI(token = token, logging = LoggingConfig(LogLevel.None))

    while (true) {
        println("Select an option:")
        println("1 - Engines")
        println("2 - Files")
        println("3 - Moderations")
        println("4 - Images")
        println("5 - Chat")
        println("6 - Whisper")
        println("7 - Tokens")
        println("8 - Assistants")
        println("0 - Quit")

        suspend fun chats() {
            println("Select an option:")
            println("1 - Chat")
            println("2 - Tool Call")
            when (val option = readlnOrNull()?.toIntOrNull()) {
                1 -> chat(openAI)
                2 -> chatFunctionCall(openAI)
                0 -> {
                    println("Exiting...")
                    return
                }
                else -> println("Invalid option selected: $option")
            }
        }

        suspend fun assistants() {
            println("Select an option:")
            println("1 - Code Interpreter")
            println("2 - Retrieval")
            println("3 - Functions")
            println("0 - Back")
            when (val option = readlnOrNull()?.toIntOrNull()) {
                1 -> assistantsCodeInterpreter(openAI)
                2 -> assistantsRetrieval(openAI)
                3 -> assistantsFunctions(openAI)
                0 -> {
                    println("Exiting...")
                    return
                }
                else -> println("Invalid option selected: $option")
            }
        }

        when (val option = readlnOrNull()?.toIntOrNull()) {
            1 -> engines(openAI)
            2 -> files(openAI)
            3 -> moderations(openAI)
            4 -> images(openAI)
            5 -> chats()
            6 -> whisper(openAI)
            7 -> tokensCount(openAI)
            8 -> assistants()
            0 -> {
                println("Exiting...")
                return@runBlocking
            }
            else -> println("Invalid option selected: $option")
        }
        println("\n----------\n") // for readability
    }
}
