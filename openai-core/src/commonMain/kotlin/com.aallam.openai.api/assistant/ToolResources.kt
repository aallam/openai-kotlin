package com.aallam.openai.api.assistant

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@BetaOpenAI
@Serializable
public data class ToolResources(
    @SerialName("file_search") public val fileSearch: FileSearchResource? = null,
    @SerialName("code_interpreter") public val codeInterpreter: CodeInterpreterResource? = null,
)

@BetaOpenAI
@Serializable
public data class FileSearchResource(
    @SerialName("vector_store_ids") val vectorStoreIds: List<String>? = null
)

@BetaOpenAI
@Serializable
public data class CodeInterpreterResource(
    @SerialName("file_ids") val fileIds: List<String>? = null
)