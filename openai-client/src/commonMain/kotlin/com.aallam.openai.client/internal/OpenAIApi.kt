package com.aallam.openai.client.internal

import com.aallam.openai.client.*
import com.aallam.openai.client.internal.api.*
import com.aallam.openai.client.internal.http.HttpRequester
import okio.FileSystem

/**
 * Implementation of [OpenAI].
 *
 * @param requester http transport layer
 * @param fileSystem access to read and write files
 */
internal class OpenAIApi(
    private val requester: HttpRequester,
    private val fileSystem: FileSystem
) : OpenAI,
    Completions by CompletionsApi(requester),
    Files by FilesApi(requester, fileSystem),
    Edits by EditsApi(requester),
    Embeddings by EmbeddingsApi(requester),
    Models by ModelsApi(requester),
    Moderations by ModerationsApi(requester),
    FineTunes by FineTunesApi(requester),
    Images by ImagesApi(requester)
