package com.aallam.openai.sample.jvm

import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI

suspend fun engines(openAI: OpenAI) {
    println("> Getting available engines...")
    openAI.models().forEach(::println)

    println("\n> Getting ada engine...")

    val ada = openAI.model(modelId = ModelId("text-ada-001"))
    println(ada)
}
