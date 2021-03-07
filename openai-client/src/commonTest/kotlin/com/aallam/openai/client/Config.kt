package com.aallam.openai.client

import com.aallam.openai.client.internal.env
import io.ktor.client.features.logging.*

internal val token: String
    get() = requireNotNull(env("OPENAI_API_KEY")) { "OPENAI_API_KEY environment variable must be set." }

internal val config: OpenAIConfig
    get() = OpenAIConfig(
        token = token,
        logLevel = LogLevel.ALL
    )


