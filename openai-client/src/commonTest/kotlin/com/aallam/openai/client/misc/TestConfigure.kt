package com.aallam.openai.client.misc

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.client.OpenAIConfig
import com.aallam.openai.client.TestOpenAI
import com.aallam.openai.client.token
import io.ktor.client.plugins.api.createClientPlugin
import kotlin.test.Test
import kotlin.test.assertTrue
import kotlin.time.Duration.Companion.minutes

class TestConfigure : TestOpenAI() {
    @Test
    fun configureClientPlugin() = test {
        val responseHeaders = mutableListOf<String>()
        val plugin = createClientPlugin("CustomHeaderPlugin") {
            onResponse { response ->
                response.headers.entries().forEach { entry ->
                    responseHeaders.add(entry.key)
                }
            }
        }
        val openAI = generateOpenAI(
            OpenAIConfig(
                token = token,
                timeout = Timeout(socket = 1.minutes)
            ) {
                install(plugin)
            }
        )

        val resModels = openAI.models()
        assertTrue { resModels.isNotEmpty() }
        assertTrue { responseHeaders.isNotEmpty() }
    }
}
