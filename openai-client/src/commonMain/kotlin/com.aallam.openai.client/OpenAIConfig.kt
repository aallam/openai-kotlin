package com.aallam.openai.client

import io.ktor.client.features.logging.LogLevel
import io.ktor.client.features.logging.Logger
import io.ktor.client.features.logging.SIMPLE

public class OpenAIConfig(
  public val token: String,
  public val logLevel: LogLevel = LogLevel.HEADERS,
  public val logger: Logger = Logger.SIMPLE,
)
