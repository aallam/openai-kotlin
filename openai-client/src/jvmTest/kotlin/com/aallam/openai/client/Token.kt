package com.aallam.openai.client

import io.ktor.client.features.logging.LogLevel

internal val token: String
  get() = requireNotNull(System.getenv("OPENAI_API_KEY")) { "OPENAI_API_KEY environment variable must be set." }

internal val config: OpenAIConfig
  get() = OpenAIConfig(
    token = token,
    logLevel = LogLevel.ALL
  )
