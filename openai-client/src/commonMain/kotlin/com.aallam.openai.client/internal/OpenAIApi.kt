package com.aallam.openai.client.internal

import com.aallam.openai.client.*
import com.aallam.openai.client.internal.api.*
import com.aallam.openai.client.internal.http.HttpTransport
import okio.FileSystem

/**
 * Implementation of [OpenAI].
 *
 * @param httpTransport http transport layer
 * @param fileSystem access to read and write files
 */
internal class OpenAIApi(
    private val httpTransport: HttpTransport,
    private val fileSystem: FileSystem
) : OpenAI,
    Completions by CompletionsApi(httpTransport),
    Files by FilesApi(httpTransport, fileSystem),
    Edits by EditsApi(httpTransport),
    Embeddings by EmbeddingsApi(httpTransport),
    Models by ModelsApi(httpTransport),
    Moderations by ModerationsApi(httpTransport)