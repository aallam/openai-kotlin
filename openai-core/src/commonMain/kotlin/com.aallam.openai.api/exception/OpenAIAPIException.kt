package com.aallam.openai.api.exception


/**
 * Represents an exception thrown when an error occurs while interacting with the OpenAI API.
 *
 * @property statusCode the HTTP status code associated with the error.
 * @property error an instance of [OpenAIError] containing information about the error that occurred.
 */
public sealed class OpenAIAPIException(
    public val statusCode: Int,
    public val error: OpenAIError,
    throwable: Throwable? = null,
) : OpenAIException(message = error.detail?.message, throwable = throwable)

/**
 * Represents an exception thrown when the OpenAI API rate limit is exceeded.
 */
public class RateLimitException(
    statusCode: Int,
    error: OpenAIError,
    throwable: Throwable? = null
) : OpenAIAPIException(statusCode, error, throwable)

/**
 * Represents an exception thrown when an invalid request is made to the OpenAI API.
 */
public class InvalidRequestException(
    statusCode: Int,
    error: OpenAIError,
    throwable: Throwable? = null
) : OpenAIAPIException(statusCode, error, throwable)

/**
 * Represents an exception thrown when an authentication error occurs while interacting with the OpenAI API.
 */
public class AuthenticationException(
    statusCode: Int,
    error: OpenAIError,
    throwable: Throwable? = null
) : OpenAIAPIException(statusCode, error, throwable)

/**
 * Represents an exception thrown when a permission error occurs while interacting with the OpenAI API.
 */
public class PermissionException(
    statusCode: Int,
    error: OpenAIError,
    throwable: Throwable? = null
) : OpenAIAPIException(statusCode, error, throwable)

/**
 * Represents an exception thrown when an unknown error occurs while interacting with the OpenAI API.
 * This exception is used when the specific type of error is not covered by the existing subclasses.
 */
public class UnknownAPIException(
    statusCode: Int,
    error: OpenAIError,
    throwable: Throwable? = null
) : OpenAIAPIException(statusCode, error, throwable)
