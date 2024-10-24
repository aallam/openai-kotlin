package com.aallam.openai.api.message

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.assistant.AssistantTool
import com.aallam.openai.api.file.FileId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * References an Attachment in the message request.
 */
@Serializable
@OptIn(BetaOpenAI::class)
public data class Attachment(
    /**
     * The ID of the file to attach to the message.
     */
    @SerialName("file_id") val fileId: FileId? = null,

    /**
     * The tools to add this file to.
     */
    @SerialName("tools") val tools: List<AssistantTool>? = null,
)

/**
 * A message attachment builder.
 */
public fun attachment(block: AttachmentBuilder.() -> Unit): Attachment = AttachmentBuilder().apply(block).build()

/**
 * A message attachment builder.
 */
public class AttachmentBuilder {
    /**
     * The ID of the file to attach to the message.
     */
    public var fileId: FileId? = null

    /**
     * The tools to add this file to.
     */
    @OptIn(BetaOpenAI::class)
    public var tools: List<AssistantTool>? = null

    /**
     * Build the attachment.
     */
    @OptIn(BetaOpenAI::class)
    public fun build(): Attachment = Attachment(fileId, tools)
}
