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

/** Runtime API exception */
public class OpenAIAPIException(
    statusCode: Int,
    body: String,
) : OpenAIException(message = "(statusCode=$statusCode, body='$body')")
