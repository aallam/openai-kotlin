package com.aallam.openai.sample.jvm

import com.aallam.openai.client.OpenAI

suspend fun files(openAI: OpenAI) {
    println("\n> Read files...")
    val files = openAI.files()
    println(files)
}
