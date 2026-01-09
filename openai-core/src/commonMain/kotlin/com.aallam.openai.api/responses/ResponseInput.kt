package com.aallam.openai.api.responses

import com.aallam.openai.api.responses.ResponseInput.ListInput
import com.aallam.openai.api.responses.ResponseInput.TextInput
import kotlinx.serialization.DeserializationStrategy
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonContentPolymorphicSerializer
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonPrimitive
import kotlin.jvm.JvmInline

/**
 * Text, image, or file inputs to the model, used to generate a response.
 *
 * Can be either a simple text string or a list of messages.
 */
@Serializable(with = InputSerializer::class)
public sealed interface ResponseInput {
    /**
     * A text input to the model, equivalent to a text input with the `user` role.
     */
    @Serializable
    @JvmInline
    public value class TextInput(public val value: String) : ResponseInput

    /**
     * A list of chat messages as input to the model.
     */
    @Serializable
    @JvmInline
    public value class ListInput(public val values: List<ResponseItem>) : ResponseInput

    public companion object {
        /**
         * Create a text input from a string.
         */
        public fun from(text: String): ResponseInput = TextInput(text)

        /**
         * Create an input list from a list of items.
         */
        public fun from(items: List<ResponseItem>): ResponseInput = ListInput(items)
    }
}

/**
 * Custom serializer for Input that handles direct string or array serialization.
 */
internal class InputSerializer : JsonContentPolymorphicSerializer<ResponseInput>(ResponseInput::class) {

    override fun selectDeserializer(element: JsonElement): DeserializationStrategy<ResponseInput> {
        return when (element) {
            is JsonPrimitive -> TextInput.serializer()
            is JsonArray -> ListInput.serializer()
            else -> throw SerializationException("Unsupported JSON element: $element")
        }
    }
}

/**
 * A text input to the model.
 *
 * @param text the text content.
 */
@Serializable
@SerialName("input_text")
public data class ResponseInputText(@SerialName("text") val text: String) : ResponseContent

/**
 * An image input to the model.
 *
 * @param imageUrl the image url.
 */
@Serializable
@SerialName("input_image")
public data class ResponseInputImage(
    /**
     * The detail level of the image to be sent to the model. One of high, low, or auto. Defaults to auto.
     * */
    @SerialName("detail") val detail: ImageDetail? = null,
    /**
     * The URL of the image to be sent to the model. A fully qualified URL or base64 encoded image in a data URL.
     * */
    @SerialName("image_url") val imageUrl: String? = null,
    /**
     * The ID of the file to be sent to the model.
     */
    @SerialName("file_id") val fileId: String? = null,
) : ResponseContent


/**
 * The detail level of the image to be sent to the model.
 */
@JvmInline
@Serializable
public value class ImageDetail(public val value: String) {
    public companion object {
        public val High: ImageDetail = ImageDetail("high")
        public val Low: ImageDetail = ImageDetail("low")
        public val Auto: ImageDetail = ImageDetail("auto")
    }
}

/**
 * A file input to the model.
 */
@Serializable
@SerialName("input_file")
public data class ResponseInputFile(

    /**
     * The content of the file to be sent to the model.
     *
     */
    @SerialName("file_data") val fileData: String? = null,

    /**
     * The ID of the file to be sent to the model.
     */
    @SerialName("file_id") val fileId: String? = null,

    /**
     * The name of the file to be sent to the model.
     */
    @SerialName("filename") val fileName: String? = null,
) : ResponseContent
