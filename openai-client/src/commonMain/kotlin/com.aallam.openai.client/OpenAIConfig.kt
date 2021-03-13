package com.aallam.openai.client

import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger

/**
 * OpenAI client configuration.
 */
public class OpenAIConfig(
    public val token: String,
    public val logLevel: LogLevel = LogLevel.Headers,
    public val logger: Logger = Logger.Simple,
)
