package com.aallam.openai.client

import com.aallam.openai.client.internal.assertMicroMachinesIntroduction
import kotlin.test.Test
import kotlin.test.assertFailsWith

class TestOfflineAudioTranscript {
    @Test
    fun acceptsTheCompleteIntroductionWithAsrFormattingVariations() {
        assertMicroMachinesIntroduction(
            "This is the Micro Machine Man, presenting the most Midget miniature motorcade of Micro Machine."
        )
        assertMicroMachinesIntroduction(
            "This is the Micromachine Man presenting the most midget miniature motorcade of micromachines."
        )
    }

    @Test
    fun rejectsTruncatedOrUnrelatedSpeech() {
        assertFailsWith<AssertionError> {
            assertMicroMachinesIntroduction("This is the Micro Machine Man presenting the most midget miniature")
        }
        assertFailsWith<AssertionError> {
            assertMicroMachinesIntroduction("This is an unrelated speech recording.")
        }
        assertFailsWith<AssertionError> {
            assertMicroMachinesIntroduction("This is the Micro Machine Man presenting the most miniature motorcade of micro machines.")
        }
    }
}
