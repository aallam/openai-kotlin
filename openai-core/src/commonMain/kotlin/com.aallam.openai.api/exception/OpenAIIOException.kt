package com.aallam.openai.api.exception;

/** An exception thrown in case of an I/O error */
public sealed class OpenAIIOException(
    throwable: Throwable? = null,
) : OpenAIException(message = throwable?.message, throwable = throwable)

/** An exception thrown in case a request times out. */
public class OpenAITimeoutException(
    throwable: Throwable
) : OpenAIIOException(throwable = throwable)

/** An exception thrown in case of an I/O error */
public class GenericIOException(
    throwable: Throwable? = null,
) : OpenAIIOException(throwable = throwable)
