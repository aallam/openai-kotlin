package com.aallam.openai.sample.jvm

import com.aallam.openai.api.moderation.ModerationRequest
import com.aallam.openai.client.OpenAI

suspend fun moderations(openAI: OpenAI) {
    println("\n> Create moderations...")
    val moderation = openAI.moderations(
        request = ModerationRequest(
            input = listOf("I want to kill them.")
        )
    )
    println(moderation)
}
