package com.aallam.openai.api.image

import com.aallam.openai.api.exception.OpenAIErrorDetails
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/** Images and metadata returned by generation or editing. */
@Serializable
public data class ImageResponse(
    /** Creation time in Unix seconds. */
    @SerialName("created") public val created: Long,
    /** Base64-encoded output images. */
    @SerialName("data") public val data: List<ImageJSON> = emptyList(),
    /** Effective background. */
    @SerialName("background") public val background: ImageBackground? = null,
    /** Effective encoding. */
    @SerialName("output_format") public val outputFormat: ImageOutputFormat? = null,
    /** Effective quality. */
    @SerialName("quality") public val quality: Quality? = null,
    /** Effective dimensions. */
    @SerialName("size") public val size: ImageSize? = null,
    /** Effective end-user safety identifier. */
    @SerialName("safety_identifier") public val safetyIdentifier: String? = null,
    /** Token accounting, when returned by the model. */
    @SerialName("usage") public val usage: ImageUsage? = null,
)

/** Token accounting for image generation or editing. */
@Serializable
public data class ImageUsage(
    /** Tokens consumed by input images/text. */
    @SerialName("input_tokens") public val inputTokens: Int,
    /** Generated tokens. */
    @SerialName("output_tokens") public val outputTokens: Int,
    /** Total tokens. */
    @SerialName("total_tokens") public val totalTokens: Int,
    /** Input token breakdown. */
    @SerialName("input_tokens_details") public val inputTokensDetails: ImageTokenDetails? = null,
    /** Output token breakdown. */
    @SerialName("output_tokens_details") public val outputTokensDetails: ImageTokenDetails? = null,
)

/** Token counts split by modality. */
@Serializable
public data class ImageTokenDetails(
    /** Text tokens. */
    @SerialName("text_tokens") public val textTokens: Int? = null,
    /** Image tokens. */
    @SerialName("image_tokens") public val imageTokens: Int? = null,
)

/** A partial, completed, or error event from image generation/editing. Check [type] before reading fields. */
@Serializable
public data class ImageEvent(
    /** Service event name, including image_generation/image_edit partial_image and completed variants. */
    @SerialName("type") public val type: String,
    /** Image data present on partial/completed events. */
    @SerialName("b64_json") public val b64JSON: String? = null,
    /** Index of the partial image. */
    @SerialName("partial_image_index") public val partialImageIndex: Int? = null,
    /** Creation time in Unix seconds. */
    @SerialName("created_at") public val createdAt: Long? = null,
    /** Background of the output. */
    @SerialName("background") public val background: ImageBackground? = null,
    /** Image encoding. */
    @SerialName("output_format") public val outputFormat: ImageOutputFormat? = null,
    /** Effective quality. */
    @SerialName("quality") public val quality: Quality? = null,
    /** Effective dimensions. */
    @SerialName("size") public val size: ImageSize? = null,
    /** Effective safety identifier. */
    @SerialName("safety_identifier") public val safetyIdentifier: String? = null,
    /** Final token accounting. */
    @SerialName("usage") public val usage: ImageUsage? = null,
    /** Nested service error, when supplied. */
    @SerialName("error") public val error: OpenAIErrorDetails? = null,
    /** Error code on an error event. */
    @SerialName("code") public val code: String? = null,
    /** Error explanation on an error event. */
    @SerialName("message") public val message: String? = null,
    /** Invalid parameter on an error event. */
    @SerialName("param") public val param: String? = null,
)
