package com.aallam.openai.api.chat

import com.aallam.openai.api.core.SortOrder
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Filters for listing stored Chat Completions. */
public data class ChatCompletionListQuery(
    /** Cursor identifying the last completion on the previous page. */
    public val after: String? = null,
    /** Page size. */
    public val limit: Int? = null,
    /** Metadata key/value filters. */
    public val metadata: Map<String, String>? = null,
    /** Model filter. */
    public val model: ModelId? = null,
    /** Creation time sort order. */
    public val order: SortOrder? = null,
)

/** Metadata update for an existing stored completion. */
@Serializable
public data class ChatCompletionUpdate(
    /** New metadata entries. */
    @SerialName("metadata") public val metadata: Map<String, String>,
)
