package com.aallam.openai.client

/** Untyped legacy references must keep their original inferred function types. */
@Suppress("UNUSED_VARIABLE")
private fun legacyChatCallableReferences(client: OpenAI) {
    val create = client::chatCompletion
    val stream = client::chatCompletions
}
