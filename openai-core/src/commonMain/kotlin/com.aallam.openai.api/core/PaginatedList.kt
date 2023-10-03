package com.aallam.openai.api.core

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class PaginatedList<T>(
    @SerialName("data") val data: List<T>,
    @SerialName("has_more") val hasMore: Boolean
) : List<T> by data
