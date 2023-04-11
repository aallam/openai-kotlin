package com.aallam.openai.client

import com.aallam.openai.api.exception.AuthenticationException
import com.aallam.openai.api.exception.OpenAIAPIException
import com.aallam.openai.api.model.ModelId
import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertIs
import kotlin.test.assertTrue

class TestException {

    @Test
    fun apiError() = runTest {
        val openAI = OpenAI("sk-123")
        val model = runCatching {
            openAI.model(ModelId("davinci"))
        }
        assertTrue(model.isFailure)
        val exception = model.exceptionOrNull() as OpenAIAPIException
        assertIs<AuthenticationException>(exception)
        assertEquals(401, exception.statusCode)
        assertEquals(
            "invalid_request_error",
            exception.error.detail?.type
        )
        assertEquals(
            "invalid_api_key",
            exception.error.detail?.code
        )
    }
}
