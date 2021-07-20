package com.aallam.openai.client

import com.aallam.openai.client.internal.ClientFileSystem
import com.aallam.openai.client.internal.OpenAIApi
import com.aallam.openai.client.internal.createHttpClient

/**
 * OpenAI API.
 */
public interface OpenAI :
    Engines,
    Completions,
    Searches,
    Classifications,
    Answers,
    Files

/**
 * Creates an instance of [OpenAI].
 *
 * @param token secret API key
 */
public fun OpenAI(token: String): OpenAI {
    val config = OpenAIConfig(token = token)
    return OpenAI(config)
}

/**
 * Creates an instance of [OpenAI].
 *
 * @param config client config
 */
public fun OpenAI(config: OpenAIConfig): OpenAI {
    val httpClient = createHttpClient(config)
    return OpenAIApi(httpClient, ClientFileSystem)
}
