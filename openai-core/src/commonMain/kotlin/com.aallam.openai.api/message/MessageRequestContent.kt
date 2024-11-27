package com.aallam.openai.api.message

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.OpenAIDsl
import com.aallam.openai.api.file.FileId
import com.aallam.openai.api.message.MessageRequestPart.ImageFilePart
import com.aallam.openai.api.message.MessageRequestPart.ImageUrlPart
import com.aallam.openai.api.message.MessageRequestPart.TextPart
import com.aallam.openai.api.message.internal.MessageRequestContentSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlin.jvm.JvmInline

/**
 * The contents of the assistant request message.
 */
@BetaOpenAI
@Serializable(with = MessageRequestContentSerializer::class)
public sealed interface MessageRequestContent {
    /**
     * The assistant message content as text.
     */
    @BetaOpenAI
    @JvmInline
    @Serializable
    public value class TextContent(public val content: String) : MessageRequestContent

    /**
     *  The assistant message content as a list of content parts.
     */
    @BetaOpenAI
    @JvmInline
    @Serializable
    public value class ListContent(public val content: List<MessageRequestPart>) : MessageRequestContent
}


@BetaOpenAI
@Serializable
public sealed interface MessageRequestPart {
    /**
     * The content of the message as text.
     */
    @BetaOpenAI
    @Serializable
    @SerialName("text")
    public data class TextPart(
        /**
         * The text content of the message value and annotations.
         */
        @SerialName("text") val text: String
    ) : MessageRequestPart

    /**
     * References an image File in the content of a message.
     */
    @BetaOpenAI
    @Serializable
    @SerialName("image_file")
    public data class ImageFilePart(
        /**
         * The Image file object of the image in the message content.
         */
        @SerialName("image_file") val imageFile: ImageFile
    ) : MessageRequestPart

    /**
     * References an image url in the content of a message.
     */
    @BetaOpenAI
    @Serializable
    @SerialName("image_url")
    public data class ImageUrlPart(
        /**
         * The Image url object of the image in the message content.
         */
        @SerialName("image_url") val imageUrl: ImageURL
    ) : MessageRequestPart

}

@OptIn(BetaOpenAI::class)
@OpenAIDsl
public class MessageRequestPartBuilder {

    private val parts = mutableListOf<MessageRequestPart>()

    /**
     * Text content part.
     *
     * @param text the text content.
     */
    public fun text(text: String) {
        this.parts += TextPart(text)
    }

    /**
     * Image file content part.
     *
     * @param id the image file.
     * @param detail the image detail.
     */
    public fun imageFile(id: FileId, detail: String? = null) {
        this.parts += ImageFilePart(ImageFile(id, detail))
    }

    /**
     * Image url content part.
     *
     * @param url the image url.
     * @param detail the image detail.
     */
    public fun imageUrl(url: String, detail: String? = null) {
        this.parts += ImageUrlPart(ImageURL(url, detail))
    }


    /**
     * Create a list of [MessageRequestPart]s.
     */
    public fun build(): List<MessageRequestPart> {
        return parts
    }
}