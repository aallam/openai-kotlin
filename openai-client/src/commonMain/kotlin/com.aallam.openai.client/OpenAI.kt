package com.aallam.openai.client

import com.aallam.openai.client.internal.ClientFileSystem
import com.aallam.openai.client.internal.OpenAIApi
import com.aallam.openai.client.internal.createHttpClient
import com.aallam.openai.client.internal.http.HttpTransport

/**
 * OpenAI API.
 */
public interface OpenAI :
    Engines,
    Completions,
    Searches,
    Classifications,
    Answers,
    Files,
    FineTunes

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
    val transport = HttpTransport(httpClient)
    return OpenAIApi(transport, ClientFileSystem)
}
