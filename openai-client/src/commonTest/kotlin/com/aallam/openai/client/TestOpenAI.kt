package com.aallam.openai.client

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import com.aallam.openai.client.internal.OpenAIApi
import com.aallam.openai.client.internal.createHttpClient
import com.aallam.openai.client.internal.env
import com.aallam.openai.client.internal.http.HttpTransport
import kotlinx.coroutines.test.TestScope
import kotlinx.coroutines.test.runTest
import kotlin.time.Duration.Companion.minutes

internal fun isLiveTestsEnabled(): Boolean = env("OPENAI_LIVE_TESTS") == "1"

internal val token: String
    get() = requireNotNull(env("OPENAI_API_KEY")) {
        "OPENAI_API_KEY environment variable must be set when OPENAI_LIVE_TESTS=1."
    }


internal fun openAIConfig(): OpenAIConfig = OpenAIConfig(
    token = token,
    logging = LoggingConfig(logLevel = LogLevel.All),
    timeout = Timeout(socket = 1.minutes),
    retry = RetryStrategy(maxRetries = 0),
)

private fun transport(config: OpenAIConfig? = null): HttpTransport {
    return HttpTransport(
        createHttpClient(
            config ?: openAIConfig()
        )
    )
}

abstract class TestOpenAI {
    internal val openAI: OpenAIApi by lazy { OpenAIApi(transport()) }

    internal fun generateOpenAI(
        config: OpenAIConfig
    ): OpenAIApi {
        return OpenAIApi(transport(config))
    }

    fun test(testBody: suspend TestScope.() -> Unit) = runTest(timeout = 1.minutes) {
        if (!isLiveTestsEnabled()) return@runTest
        testBody()
    }
}
