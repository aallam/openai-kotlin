package com.aallam.openai.client

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import kotlin.time.Duration
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
 * @param host OpenAI host configuration
 * @param proxy HTTP proxy url
 * @param host OpenAI host configuration.
 * @param retry http retry strategy
 */
public class OpenAIConfig(
    public val token: String,
    public val logLevel: LogLevel = LogLevel.Headers,
    public val logger: Logger = Logger.Simple,
    public val timeout: Timeout = Timeout(socket = 30.seconds),
    public val organization: String? = null,
    public val headers: Map<String, String> = emptyMap(),
    public val host: OpenAIHost = OpenAIHost.OpenAI,
    public val proxy: ProxyConfig? = null,
    public val retry: RetryStrategy = RetryStrategy.Exponential(),
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

/** Proxy configuration. */
public sealed interface ProxyConfig {

    /** Creates an HTTP proxy from [url]. */
    public class Http(public val url: String) : ProxyConfig

    /** Create socks proxy from [host] and [port]. */
    public class Socks(public val host: String, public val port: Int) : ProxyConfig
}


/**
 * Specifies the retry strategy
 */
public sealed interface RetryStrategy {

    /** The maximum amount of retries to perform for a request. */
    public val maxRetries: Int

    /**
     * Specifies an exponential delay between retries
     *
     * @param maxRetries the maximum amount of retries to perform for a request
     * @param base exponential base value
     * @param maxDelay max retry delay
     */
    public class Exponential(
        public override val maxRetries: Int = 3,
        public val base: Double = 2.0,
        public val maxDelay: Duration = 60.seconds,
    ) : RetryStrategy

    /**
     * Specifies a constant delay between retries
     *
     * @param maxRetries the maximum amount of retries to perform for a request
     * @param delay retry delay duration
     */
    public class Constant(
        public override val maxRetries: Int = 3,
        public val delay: Duration = 1.seconds,
    ) : RetryStrategy

    /** Disables retry. */
    public object NoRetry : RetryStrategy {
        override val maxRetries: Int = 0
    }
}