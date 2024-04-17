package com.aallam.openai.api.vectorstore

import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The identifier of a vector store.
 */
@JvmInline
@Serializable
public value class VectorStoreId(public val id: String)
