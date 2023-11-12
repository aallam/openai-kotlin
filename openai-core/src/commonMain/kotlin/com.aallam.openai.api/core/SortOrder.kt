package com.aallam.openai.api.core

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

@Serializable
@JvmInline
public value class SortOrder(public val order: String) {
    public companion object {
        public val Ascending: SortOrder = SortOrder("asc")
        public val Descending: SortOrder = SortOrder("desc")
    }
}
