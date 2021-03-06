package com.aallam.openai.api.engine

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Response to the engines list query.
 */
@Serializable
public class EnginesResponse(
  /**
   * List containing the actual results.
   */
  @SerialName("data") public val data: List<Engine>,
)
