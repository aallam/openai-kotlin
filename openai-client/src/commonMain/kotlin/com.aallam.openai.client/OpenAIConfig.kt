package com.aallam.openai.client

import com.aallam.openai.api.http.Timeout
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import io.ktor.client.HttpClientConfig
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

/**
 * OpenAI client configuration.
 *
 * @param token OpenAI Token
 * @param logging client logging configuration
 * @param timeout http client timeout
 * @param headers extra http headers
 * @param organization OpenAI organization ID
 * @param host OpenAI host configuration
 * @param proxy HTTP proxy url
 * @param host OpenAI host configuration.
 * @param retry rate limit retry configuration
 * @param configure additional custom client configuration
 */
public class OpenAIConfig(
    public val token: String,
    public val logging: LoggingConfig = LoggingConfig(),
    public val timeout: Timeout = Timeout(socket = 30.seconds),
    public val organization: String? = null,
    public val headers: Map<String, String> = emptyMap(),
    public val host: OpenAIHost = OpenAIHost.OpenAI,
    public val proxy: ProxyConfig? = null,
    public val retry: RetryStrategy = RetryStrategy(),
    public val configure: HttpClientConfig<*>.() -> Unit = {}
) {

    @Deprecated(
        message = "Use primary constructor with LoggingConfig instead.",
        replaceWith = ReplaceWith(
            expression = "OpenAIConfig(token, LoggingConfig(logLevel, logger), timeout, organization, headers, host, proxy, retry)",
            imports = ["com.aallam.openai.api.logging.Logger", "com.openai.config.LoggingConfig"],
        )
    )
    public constructor(
        token: String,
        logLevel: LogLevel = LogLevel.Headers,
        logger: Logger = Logger.Simple,
        timeout: Timeout = Timeout(socket = 30.seconds),
        organization: String? = null,
        headers: Map<String, String> = emptyMap(),
        host: OpenAIHost = OpenAIHost.OpenAI,
        proxy: ProxyConfig? = null,
        retry: RetryStrategy = RetryStrategy(),
        configure: HttpClientConfig<*>.() -> Unit = {}
    ) : this(
        token = token,
        logging = LoggingConfig(
            logLevel = logLevel,
            logger = logger,
        ),
        timeout = timeout,
        organization = organization,
        headers = headers,
        host = host,
        proxy = proxy,
        retry = retry,
        configure = configure,
    )
}

/**
 * A class to configure the OpenAI host.
 * It provides a mechanism to customize the base URL and additional query parameters used in OpenAI API requests.
 */
public class OpenAIHost(

    /**
     * Base URL configuration.
     * This is the root URL that will be used for all API requests to OpenAI.
     * The URL can include a base path, but in that case, the base path should always end with a `/`.
     * For example, a valid base URL would be "https://api.openai.com/v1/"
     */
    public val baseUrl: String,

    /**
     * Additional query parameters to be appended to all API requests to OpenAI.
     * These can be used to provide additional configuration or context for the API requests.
     */
    public val queryParams: Map<String, String> = emptyMap()
) {

    public companion object {
        /**
         * A pre-configured instance of [OpenAIHost] with the base URL set as `https://api.openai.com/v1/`.
         */
        public val OpenAI: OpenAIHost = OpenAIHost(baseUrl = "https://api.openai.com/v1/")

        /**
         * Creates an instance of [OpenAIHost] configured for Azure hosting with the given resource name, deployment ID,
         * and API version.
         *
         * @param resourceName The name of your Azure OpenAI Resource.
         * @param deploymentId The name of your model deployment.
         * @param apiVersion The API version to use for this operation. This parameter should follow the YYYY-MM-DD format.
         */
        public fun azure(resourceName: String, deploymentId: String, apiVersion: String): OpenAIHost =
            OpenAIHost(
                baseUrl = "https://$resourceName.openai.azure.com/openai/deployments/$deploymentId/",
                queryParams = mapOf("api-version" to apiVersion),
            )
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
 *
 * @param maxRetries the maximum amount of retries to perform for a request
 * @param base retry base value
 * @param maxDelay max retry delay
 */
public class RetryStrategy(
    public val maxRetries: Int = 3,
    public val base: Double = 2.0,
    public val maxDelay: Duration = 60.seconds,
)

/**
 * Defines the configuration parameters for logging.
 *
 * @property logLevel the level of logging to be used by the HTTP client.
 * @property logger the logger instance to be used by the HTTP client.
 * @property sanitize flag indicating whether to sanitize sensitive information (i.e., authorization header) in the logs
 */
public class LoggingConfig(
    public val logLevel: LogLevel = LogLevel.Headers,
    public val logger: Logger = Logger.Simple,
    public val sanitize: Boolean = true,
)
