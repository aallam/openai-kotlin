package com.aallam.openai.client

import com.aallam.openai.api.engine.Engine
import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.api.engine.EnginesResponse
import com.aallam.openai.api.search.SearchRequest
import com.aallam.openai.api.search.SearchResponse
import com.aallam.openai.client.internal.OpenAIApi

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
  ): SearchResponse

  /**
   * Lists the currently available engines, and provides basic information about each one such as
   * the owner and availability.
   */
  public suspend fun engines(): EnginesResponse

  /**
   * Retrieves an engine instance, providing basic information about the engine such as the owner
   * and availability.
   */
  public suspend fun engine(engineId: EngineId): Engine

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
