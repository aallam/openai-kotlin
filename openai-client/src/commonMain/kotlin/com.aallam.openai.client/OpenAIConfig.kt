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
 * @param headers extra http headers
 * @param organization OpenAI organization ID
 * @param host OpenAI host configuration.
 */
public class OpenAIConfig(
    public val token: String,
    public val logLevel: LogLevel = LogLevel.Headers,
    public val logger: Logger = Logger.Simple,
    public val timeout: Timeout = Timeout(socket = 30.seconds),
    public val organization: String? = null,
    public val headers: Map<String, String> = emptyMap(),
    public val host: OpenAIHost = OpenAIHost.OpenAI,
)

/**
 * OpenAI host configuration.
 */
public class OpenAIHost(
    /** Base URL configuration.*/
    public val baseUrl: String,
    /** Additional query parameters */
    public val queryParams: Map<String, String> = emptyMap()
) {
    public companion object {
        public val OpenAI: OpenAIHost = OpenAIHost("https://api.openai.com")
    }
}
