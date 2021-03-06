package com.aallam.openai.api.search

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * A search result for a single scored documents.
 * [documentation](https://beta.openai.com/docs/api-reference/search)
 */
@Serializable
public class SearchResponse(
  /**
   * The position of this document in the request list.
   */
  @SerialName("document") public val document: Int,
  /**
   * The type of object returned, should be "search_result".
   */
  @SerialName("object") public val type: String,
  /**
   * The similarity score is a positive score that usually ranges from 0 to 300 (but can sometimes
   * go higher), where a score above 200 usually means the document is semantically similar to
   * the query.
   */
  @SerialName("score") public val score: Double,
)
