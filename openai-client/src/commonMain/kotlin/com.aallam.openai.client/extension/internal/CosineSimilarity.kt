package com.aallam.openai.client.extension.internal

import kotlin.math.pow
import kotlin.math.sqrt

internal object Cosine {

    /**
     * Compute similarity between two [Double] vectors.
     */
    fun similarity(vec1: List<Double>, vec2: List<Double>): Double {
        if (vec1 == vec2) return 1.0
        return (vec1 dot vec2) / (norm(vec1) * norm(vec2))
    }

    /**
     * Compute distance between two [Double] vectors.
     */
    fun distance(vec1: List<Double>, vec2: List<Double>): Double {
        return 1.0 - similarity(vec1, vec2)
    }

    /** Dot product */
    private infix fun List<Double>.dot(vector: List<Double>): Double {
        return zip(vector).fold(0.0) { acc, (i, j) -> acc + (i * j) }
    }

    /** Compute the norm L2 : sqrt(sum(v²)). */
    private fun norm(vector: List<Double>): Double {
        val sum = vector.fold(0.0) { acc, cur -> acc + cur.pow(2) }
        return sqrt(sum)
    }
}
