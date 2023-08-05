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
        println("2 - Files")
        println("3 - Moderations")
        println("4 - Images")
        println("5 - Chat")
        println("6 - Chat (w/ Function)")
        println("7 - Whisper")
        println("0 - Quit")

        val option = readlnOrNull()?.toIntOrNull()
        when (option) {
            1 -> engines(openAI)
            2 -> files(openAI)
            3 -> moderations(openAI)
            4 -> images(openAI)
            5 -> chat(openAI)
            6 -> chatFunctionCall(openAI)
            7 -> whisper(openAI)
            0 -> {
                println("Exiting...")
                return@runBlocking
            }
            else -> println("Invalid option selected: $option")
        }
        println("\n----------\n") // for readability
    }
}
