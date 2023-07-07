package com.aallam.openai.sample.jvm

import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.LoggingConfig
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.runBlocking

fun main() = runBlocking {
    val apiKey = System.getenv("OPENAI_API_KEY")
    val token = requireNotNull(apiKey) { "OPENAI_API_KEY environment variable must be set." }
    val openAI = OpenAI(token = token, logging = LoggingConfig(LogLevel.All))

    while (true) {
        println("Select an option:")
        println("1 - Engines")
        println("2 - Completion")
        println("3 - Files")
        println("4 - Moderations")
        println("5 - Images")
        println("6 - Chat")
        println("61 - Chat with Headers")
        println("7 - Chat (w/ Function)")
        println("8 - Whisper")
        println("81 - Whisper with Headers")
        println("0 - Quit")

        when (readlnOrNull()?.toIntOrNull()) {
            1 -> engines(openAI)
            2 -> completion(openAI)
            3 -> files(openAI)
            4 -> moderations(openAI)
            5 -> images(openAI)
            6 -> chat(openAI)
            61 -> chatWithHeaders(openAI)
            7 -> chatFunctionCall(openAI)
            8 -> whisper(openAI)
            81 -> whisperWithHeaders(openAI)
            0 -> {
                println("Exiting...")
                return@runBlocking
            }
            else -> println("Invalid option selected")
        }
        println("\n----------\n") // for readability
    }
}
