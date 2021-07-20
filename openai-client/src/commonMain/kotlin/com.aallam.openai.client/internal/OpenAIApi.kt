package com.aallam.openai.client.internal

import com.aallam.openai.client.*
import com.aallam.openai.client.internal.api.*
import io.ktor.client.*
import okio.FileSystem

/**
 * Implementation of [OpenAI].
 *
 * @param httpClient http client
 * @param fileSystem access to read and write files
 */
internal class OpenAIApi(
    private val httpClient: HttpClient,
    private val fileSystem: FileSystem
) : OpenAI,
    Engines by EnginesApi(httpClient),
    Completions by CompletionsApi(httpClient),
    Searches by SearchesApi(httpClient),
    Classifications by ClassificationsApi(httpClient),
    Answers by AnswersApi(httpClient),
    Files by FilesApi(httpClient, fileSystem)
