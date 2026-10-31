package com.aallam.openai.client.internal

import kotlin.test.assertTrue

internal fun assertMicroMachinesIntroduction(text: String) {
    // ASR can vary punctuation, brand spacing, and the final plural without losing the spoken introduction.
    val introduction = "This is the Micro Machine Man presenting the most midget miniature motorcade of Micro Machine"
    fun normalized(value: String): String = value.lowercase().filter { it.isLetterOrDigit() }
    assertTrue(normalized(text).startsWith(normalized(introduction)), "Unexpected transcription: $text")
}
