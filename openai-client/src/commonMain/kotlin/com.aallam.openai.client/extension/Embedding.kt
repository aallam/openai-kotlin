package com.aallam.openai.client.extension

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.embedding.Embedding
import com.aallam.openai.client.extension.internal.Cosine

/**
 * Compute the similarity of two embeddings using [cosine similarity](https://en.wikipedia.org/wiki/Cosine_similarity).
 */
@ExperimentalOpenAI
public fun Embedding.similarity(other: Embedding): Double = Cosine.similarity(embedding, other.embedding)

/**
 * Calculate the distance between two embeddings, corresponding to 1.0 - similarity.
 */
@ExperimentalOpenAI
public fun Embedding.distance(other: Embedding): Double = Cosine.distance(embedding, other.embedding)
