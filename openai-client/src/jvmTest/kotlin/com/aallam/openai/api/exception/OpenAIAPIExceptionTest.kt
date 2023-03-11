package com.aallam.openai.api.exception

import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.runBlocking
import org.junit.Assert.*
import org.junit.Test

class OpenAIAPIExceptionTest {

    /**
     *
     ```
        {
        "error": {
            "message": "Incorrect API key provided: 12. You can find your API key at https://platform.openai.com/account/api-keys.",
            "type": "invalid_request_error",
            "param": null,
            "code": "invalid_api_key"
            }
        }
     ```
     */
    @Test
    fun `test OpenAI API Error`() = runBlocking {
        val openAI = OpenAI("sk-123")
        val model = kotlin.runCatching {
            openAI.model(ModelId("davinci"))
        }
        assert(model.isFailure)
        val exception = model.exceptionOrNull()?.cause as OpenAIAPIException
        assertEquals(401, exception.statusCode)
        assertEquals(
            "invalid_request_error",
            exception.openAIAPIError.error?.type
        )
        assertEquals(
            "invalid_api_key",
            exception.openAIAPIError.error?.code
        )
    }
}