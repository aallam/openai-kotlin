package com.aallam.openai.api.assistant

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.file.FileId
import kotlinx.serialization.SerialName

/**
 * File attached to an assistant.
 */
@Deprecated("For v1 API only")
@BetaOpenAI
public data class AssistantFile(
    /**
     * The identifier, which can be referenced in API endpoints.
     */
    @SerialName("id") public val id: FileId,

    /**
     * The Unix timestamp (in seconds) for when the assistant file was created.
     */
    @SerialName("created_at") public val createdAt: Int,

    /**
     * The assistant ID that the file is attached to.
     */
    @SerialName("assistant_id") public val assistantId: AssistantId
)
