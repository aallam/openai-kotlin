package com.aallam.openai.api

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A wrapper class to fit the OpenAI engine and search endpoints.
 */
@Serializable
public class OpenAIResponse<T>(
  /**
   * List containing the actual results.
   */
  @SerialName("data") public val data: List<T>,

  /**
   * The type of object returned, should always be "list".
   */
  @SerialName("object") public val type: String,

  /**
   * The model that executed the request.
   */
  @SerialName("model") public val model: String,
)
