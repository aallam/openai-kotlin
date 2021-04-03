package com.aallam.openai.client

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.answer.Answer
import com.aallam.openai.api.answer.AnswerRequest
import com.aallam.openai.api.classification.Classification
import com.aallam.openai.api.classification.ClassificationRequest
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.completion.TextCompletion
import com.aallam.openai.api.engine.Engine
import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.api.file.File
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.file.FileRequest
import com.aallam.openai.api.search.SearchRequest
import com.aallam.openai.api.search.SearchResult
import com.aallam.openai.client.internal.OpenAIApi
import kotlinx.coroutines.flow.Flow


/**
 * OpenAI API.
 */
public interface OpenAI {

    /**
     * Performs a semantic search over a list of documents.
     * Response includes the list of scored documents (in the same order that they were passed in).
     */
    public suspend fun search(
        engineId: EngineId,
        request: SearchRequest
    ): List<SearchResult>

    /**
     * Lists the currently available engines, and provides basic information about each one such as
     * the owner and availability.
     */
    public suspend fun engines(): List<Engine>

    /**
     * Retrieves an engine instance, providing basic information about the engine such as the owner
     * and availability.
     */
    public suspend fun engine(engineId: EngineId): Engine

    /**
     * This is the main endpoint of the API. Returns the predicted completion for the given prompt,
     * and can also return the probabilities of alternative tokens at each position if requested.
     */
    @Deprecated(message = "renamed to `completion`", replaceWith = ReplaceWith("completion(engineId, request)"))
    public suspend fun createCompletion(engineId: EngineId, request: CompletionRequest? = null): TextCompletion {
        return completion(engineId, request)
    }

    /**
     * This is the main endpoint of the API. Returns the predicted completion for the given prompt,
     * and can also return the probabilities of alternative tokens at each position if requested.
     */
    public suspend fun completion(engineId: EngineId, request: CompletionRequest? = null): TextCompletion

    /**
     * Stream variant of [completion].
     */
    public fun completions(engineId: EngineId, request: CompletionRequest? = null): Flow<TextCompletion>

    /**
     * The endpoint first searches over the labeled examples to select the ones most relevant for the particular query.
     * Then, the relevant examples are combined with the query to construct a prompt to produce the final label via
     * the completions endpoint.
     */
    @ExperimentalOpenAI
    public suspend fun classifications(request: ClassificationRequest): Classification

    /**
     * Answers the specified question using the provided documents and examples.
     *
     * The endpoint first searches over provided documents or files to find relevant context.
     * The relevant context is combined with the provided examples and question to create the prompt for [completion].
     */
    @ExperimentalOpenAI
    public suspend fun answers(request: AnswerRequest): Answer

    /**
     * Upload a file that contains document(s) to be used across various endpoints/features.
     * Currently, the size of all the files uploaded by one organization can be up to 1 GB.
     */
    public suspend fun file(request: FileRequest): File

    /**
     * Returns a list of files that belong to the user's organization.
     */
    public suspend fun files(): List<File>

    /**
     * Returns information about a specific file.
     */
    public suspend fun file(fileId: FileId): File?

    /**
     * Delete a file. Only owners of organizations can delete files currently.
     */
    public suspend fun delete(fileId: FileId)

    public companion object
}

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
    return OpenAIApi(config)
}
