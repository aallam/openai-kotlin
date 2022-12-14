package com.aallam.openai.client

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import kotlin.time.Duration.Companion.seconds

/**
 * OpenAI client configuration.
 *
 * @param token OpenAI Token
 * @param logger http client logging level
 * @param logLevel http client logger
 * @param timeout http client timeout
 */
public class OpenAIConfig(
    public val token: String,
    public val logLevel: LogLevel = LogLevel.Headers,
    public val logger: Logger = Logger.Simple,
    public val timeout: Timeout = Timeout(socket = 30.seconds),
)
