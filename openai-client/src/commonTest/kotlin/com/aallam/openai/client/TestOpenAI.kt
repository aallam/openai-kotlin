package com.aallam.openai.client

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.internal.OpenAIApi
import com.aallam.openai.client.internal.createHttpClient
import com.aallam.openai.client.internal.env
import com.aallam.openai.client.internal.http.HttpTransport
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.time.Duration.Companion.minutes

internal val token: String
    get() = requireNotNull(env("OPENAI_API_KEY")) { "OPENAI_API_KEY environment variable must be set." }

internal val transport = HttpTransport(
    createHttpClient(
        OpenAIConfig(
            token = token,
            logging = LoggingConfig(logLevel = LogLevel.All),
            timeout = Timeout(socket = 1.minutes),
        )
    )
)

abstract class TestOpenAI {
    internal val openAI = OpenAIApi(transport)

    fun test(testBody: suspend TestScope.() -> Unit) = runTest(timeout = 1.minutes, testBody = testBody)
}
