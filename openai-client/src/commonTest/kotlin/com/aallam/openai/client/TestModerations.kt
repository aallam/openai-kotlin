package com.aallam.openai.client

import com.aallam.openai.api.moderation.moderationRequest
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertTrue

class TestModerations : TestOpenAI() {

    @Test
    fun moderations() = runTest {
        val response = openAI.moderations(
            request = moderationRequest {
                input = listOf("I want to kill them.")
            }
        )

        val result = response.results.first()
        assertTrue(result.flagged)
    }
}
