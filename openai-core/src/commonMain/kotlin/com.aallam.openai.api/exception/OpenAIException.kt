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

/** An exception thrown in case of a server error */
public class OpenAIServerException(
    throwable: Throwable? = null,
) : OpenAIException(message = throwable?.message, throwable = throwable)
