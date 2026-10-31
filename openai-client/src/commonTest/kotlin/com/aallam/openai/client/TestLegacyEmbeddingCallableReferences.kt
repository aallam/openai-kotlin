package com.aallam.openai.client

/** Untyped legacy references must keep their original inferred function type. */
@Suppress("UNUSED_VARIABLE")
private fun legacyEmbeddingCallableReferences(client: OpenAI) {
    val create = client::embeddings
}
