package com.aallam.openai.api.assistant

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.vectorstore.VectorStoreId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@BetaOpenAI
@Serializable
public data class ToolResources(
    @SerialName("file_search") public val fileSearch: FileSearchResources? = null,
    @SerialName("code_interpreter") public val codeInterpreter: CodeInterpreterResources? = null,
)

@BetaOpenAI
@Serializable
public data class FileSearchResources(
    @SerialName("vector_store_ids") val vectorStoreIds: List<VectorStoreId>? = null
)

@BetaOpenAI
@Serializable
public data class CodeInterpreterResources(
    @SerialName("file_ids") val fileIds: List<FileId>? = null
)
