package com.aallam.openai.client

@Suppress("UNUSED_VARIABLE", "UNUSED_PARAMETER")
private fun legacyCallableReferences(client: OpenAI) {
    val embed = client::embeddings
    val moderate = client::moderations
    val speak = client::speech
    val transcribe = client::transcription
    val createChat = client::chatCompletion
    val streamChat = client::chatCompletions
    val inputItems = client::responseInputItems
    val updateStore = client::updateVectorStore
    val createFileBatch = client::createVectorStoreFilesBatch
}
