package com.aallam.openai.api.exception

/** OpenAI client exception */
public sealed class OpenAIException(
    message: String? = null,
    throwable: Throwable? = null
) : RuntimeException(message, throwable)

/** Runtime Http Client exception */
public class OpenAIHttpException(
    throwable: Throwable? = null,
) : OpenAIException(throwable?.message, throwable)

/**
 * An exception thrown when an error occurs while interacting with the OpenAI API.
 */
public class OpenAIAPIException(
    /** Http status code **/
    public val statusCode: Int,
    /** Contains information about the error that occurred.*/
    public val error: OpenAIError,
    throwable: Throwable? = null,
) : OpenAIException(message = error.detail?.message, throwable = throwable)

/** An exception thrown in case a request times out. */
public class OpenAITimeoutException(
    throwable: Throwable
) : OpenAIException(message = throwable.message, throwable = throwable)

/** An exception thrown in case of a server error */
public class OpenAIServerException(
    throwable: Throwable? = null,
) : OpenAIException(message = throwable?.message, throwable = throwable)

/** An exception thrown at the client level */
public class OpenAIClientException(
    message: String? = null,
    throwable: Throwable? = null,
) : OpenAIException(message ?: throwable?.message, throwable)
