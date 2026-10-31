package com.aallam.openai.api.vectorstore

import kotlinx.serialization.EncodeDefault
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** File chunking strategy. Omit [static] for automatic chunking. */
@Serializable
public data class ChunkingStrategy(
    /** Either `auto` or `static`. */
    @EncodeDefault(EncodeDefault.Mode.ALWAYS)
    @SerialName("type") public val type: String = "auto",
    /** Static chunk sizes, required when type is `static`. */
    @SerialName("static") public val static: StaticChunking? = null,
)

/** Sizes used when splitting a file into overlapping chunks. */
@Serializable
public data class StaticChunking(
    /** Maximum tokens per chunk. */
    @SerialName("max_chunk_size_tokens") public val maxChunkSizeTokens: Int,
    /** Tokens shared by adjacent chunks. */
    @SerialName("chunk_overlap_tokens") public val chunkOverlapTokens: Int,
)
