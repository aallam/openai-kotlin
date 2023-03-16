package com.aallam.openai.api.exception

/** OpenAI client exception */
public sealed class OpenAIException(
    message: String? = null,
    throwable: Throwable? = null
) : RuntimeException(message, throwable)

/** Runtime Http Client exception */
public class OpenAIHttpException(
    message: String? = null,
    throwable: Throwable? = null
) : OpenAIException(message, throwable)

/**
 * An exception thrown when an error occurs while interacting with the OpenAI API.
 */
public class OpenAIAPIException(
    /** Http status code **/
    public val statusCode: Int,
    /** Contains information about the error that occurred.*/
    public val error: OpenAIError,
) : OpenAIException(message = error.detail?.message)
