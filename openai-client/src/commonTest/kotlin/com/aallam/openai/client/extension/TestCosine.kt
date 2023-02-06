package com.aallam.openai.client.extension

import com.aallam.openai.client.extension.internal.Cosine
import kotlin.test.Test
import kotlin.test.assertEquals

class TestCosine {

    @Test
    fun similarity() {
        val a = listOf(3.0, 45.0, 7.0, 2.0)
        val b = listOf(2.0, 54.0, 13.0, 15.0)
        val similarity = Cosine.similarity(a, b)
        assertEquals(0.9722, similarity, 0.0001)
    }

    @Test
    fun distance() {
        val a = listOf(3.0, 45.0, 7.0, 2.0)
        val b = listOf(2.0, 54.0, 13.0, 15.0)
        val distance = Cosine.distance(a, b)
        assertEquals(0.0277, distance, 0.0001)
    }
}
