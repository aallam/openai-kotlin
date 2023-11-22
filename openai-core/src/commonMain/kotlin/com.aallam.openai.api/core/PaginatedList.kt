package com.aallam.openai.api.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class PaginatedList<T>(
    @SerialName("data") val data: List<T>,
    @SerialName("has_more") val hasMore: Boolean? = null,
    @SerialName("first_id") val firstId: String? = null,
    @SerialName("last_id") val lastId: String? = null,
) : List<T> by data
