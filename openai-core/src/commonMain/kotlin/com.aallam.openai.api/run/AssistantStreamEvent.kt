package com.aallam.openai.api.run

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.chat.ImagePart
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.image.ImageURL
import com.aallam.openai.api.message.*
import com.aallam.openai.api.thread.Thread
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.PrimitiveKind
import kotlinx.serialization.descriptors.PrimitiveSerialDescriptor
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlin.reflect.KClass

/**
 * Represents an event emitted when streaming a run.
 * @property rawType the raw string of the event type.
 * @property type the type of the event or [AssistantStreamEventType.UNKNOWN] if unrecognized.
 * @property data the string serialized representation of the data for the event.
 */
@BetaOpenAI
@Serializable
public data class AssistantStreamEvent(
    @SerialName("rawType") val rawType: String?,
    @SerialName("type") val type: AssistantStreamEventType,
    @SerialName("data") val data: String?
)

/**
 * Represents a run step delta i.e. any changed fields on a run step during streaming.
 * @property id the identifier of the run step, which can be referenced in API endpoints.
 * @property object the object type, which is always thread.run.step.delta.
 * @property delta the delta containing the fields that have changed on the run step.
 */
@BetaOpenAI
@Serializable
public data class RunStepDelta(
    @SerialName("id") val id: RunStepId,
    @SerialName("object") val `object`: String,
    @SerialName("delta") val delta: RunStepDeltaData
)

/**
 * The delta containing the fields that have changed on the run step.
 * @property stepDetails the details of the run step.
 */
@BetaOpenAI
@Serializable
public data class RunStepDeltaData(
    @SerialName("step_details") val stepDetails: RunStepDetails
)

/**
 * Represents a message delta i.e. any changed fields on a message during streaming.
 * @param id the identifier of the message, which can be referenced in API endpoints.
 * @param object the object type, which is always thread.message.delta.
 * @param delta the delta containing the fields that have changed on the message.
 */
@BetaOpenAI
@Serializable
public data class MessageDelta(
    @SerialName("id") val id: MessageId,
    @SerialName("object") val `object`: String,
    @SerialName("delta") val delta: MessageDeltaData
)

/**
 * The delta containing the fields that have changed on the message.
 * @param role the entity that produced the message. One of user or assistant.
 * @param content the content of the message in array of text and/or image files and/or refusals and/or image urls.
 */
@BetaOpenAI
@Serializable
public data class MessageDeltaData(
    @SerialName("role") val role: Role,
    @SerialName("content") val content: List<MessageDeltaContent>
)

/**
 * The content of the message in array of text and/or images.
 */
@BetaOpenAI
@Serializable
public sealed interface MessageDeltaContent {

    /**
     * The text data that contains the text that has been added to the message.
     * @param text the field containing the text message.
     */
    @BetaOpenAI
    @Serializable
    @SerialName("text")
    public data class Text(
        @SerialName("text") val text: TextDeltaContent
    ): MessageDeltaContent

    /**
     * The image data that contains the image file field.
     * @param imageFile the field containing the image file.
     */
   @BetaOpenAI
   @Serializable
   @SerialName("image_file")
   public data class Image(
       @SerialName("image_file") val imageFile: ImageFile
   ): MessageDeltaContent

    /**
     * The refusal content that is part of a message.
     * @param refusal the string containing the refusal.
     */
    @BetaOpenAI
    @Serializable
    @SerialName("refusal")
    public data class Refusal(
        @SerialName("refusal") val refusal: String
    ): MessageDeltaContent

    /**
     * References an image URL in the content of a message.
     * @param imageURL the object of type imageURL.
     */
    @BetaOpenAI
    @Serializable
    @SerialName("image_url")
    public data class ImageURL(
        @SerialName("image_url") val imageURL: ImagePart.ImageURL
    ): MessageDeltaContent

}

/**
 * The text content that is part of a message.
 * @param index The index of the content part in the message.
 * @param value The data that makes up the text.
 * @param annotations The annotations for the text.
 */
@BetaOpenAI
@Serializable
public data class TextDeltaContent(
    @SerialName("index") val index: Int,
    @SerialName("value") val value: String,
    @SerialName("annotations") val annotations: List<TextAnnotation>
)

/**
 * Represents an event type emitted when streaming a Run.
 * @property event the string representation of event type.
 * @property dataType the type of the data.
 * @property serializer the serializer corresponding to the data type.
 */
@BetaOpenAI
@Serializable(with = AssistantStreamEventTypeSerializer::class)
public enum class AssistantStreamEventType(
    public val event: String,
    @Suppress("MemberVisibilityCanBePrivate") public val dataType: KClass<*>,
    public val serializer: KSerializer<*>
) {

    /**
     * Occurs when a new thread is created.
     */
    THREAD_CREATED("thread.created", Thread::class, Thread.serializer()),

    /**
     * Occurs when a new run is created.
     */
    THREAD_RUN_CREATED("thread.run.created", Run::class, Run.serializer()),

    /**
     * Occurs when a run moves to a queued status.
     */
    THREAD_RUN_QUEUED("thread.run.queued", Run::class, Run.serializer()),

    /**
     * Occurs when a run moves to an in_progress status.
     */
    THREAD_RUN_IN_PROGRESS("thread.run.in_progress", Run::class, Run.serializer()),

    /**
     * Occurs when a run moves to a requires_action status.
     */
    THREAD_RUN_REQUIRES_ACTION("thread.run.requires_action", Run::class, Run.serializer()),

    /**
     * Occurs when a run is completed.
     */
    THREAD_RUN_COMPLETED("thread.run.completed", Run::class, Run.serializer()),

    /**
     * Occurs when a run ends with status incomplete.
     */
    THREAD_RUN_INCOMPLETE("thread.run.incomplete", Run::class, Run.serializer()),

    /**
     * Occurs when a run fails.
     */
    THREAD_RUN_FAILED("thread.run.failed", Run::class, Run.serializer()),

    /**
     * Occurs when a run moves to a cancelling status.
     */
    THREAD_RUN_CANCELLING("thread.run.cancelling", Run::class, Run.serializer()),

    /**
     * Occurs when a run is cancelled.
     */
    THREAD_RUN_CANCELLED("thread.run.cancelled", Run::class, Run.serializer()),

    /**
     * Occurs when a run expires.
     */
    THREAD_RUN_EXPIRED("thread.run.expired", Run::class, Run.serializer()),

    /**
     * Occurs when a run step is created.
     */
    THREAD_RUN_STEP_CREATED("thread.run.step.created", RunStep::class, RunStep.serializer()),

    /**
     * Occurs when a run step moves to an in_progress state.
     */
    THREAD_RUN_STEP_IN_PROGRESS("thread.run.step.in_progress", RunStep::class, RunStep.serializer()),

    /**
     * Occurs when parts of a run step are being streamed.
     */
    THREAD_RUN_STEP_DELTA("thread.run.step.delta", RunStepDelta::class, RunStepDelta.serializer()),

    /**
     * Occurs when a run step is completed.
     */
    THREAD_RUN_STEP_COMPLETED("thread.run.step.completed", RunStep::class, RunStep.serializer()),

    /**
     * Occurs when a run step fails.
     */
    THREAD_RUN_STEP_FAILED("thread.run.step.failed", RunStep::class, RunStep.serializer()),

    /**
     * Occurs when a run step is cancelled.
     */
    THREAD_RUN_STEP_CANCELLED("thread.run.step.cancelled", RunStep::class, RunStep.serializer()),

    /**
     * Occurs when a run step expires.
     */
    THREAD_RUN_STEP_EXPIRED("thread.run.step.expired", RunStep::class, RunStep.serializer()),

    /**
     * Occurs when a message is created.
     */
    THREAD_MESSAGE_CREATED("thread.message.created", Message::class, Message.serializer()),

    /**
     * Occurs when a message moves to an in_progress state.
     */
    THREAD_MESSAGE_IN_PROGRESS("thread.message.in_progress", Message::class, Message.serializer()),

    /**
     * Occurs when parts of a Message are being streamed.
     */
    THREAD_MESSAGE_DELTA("thread.message.delta", MessageDelta::class, MessageDelta.serializer()),

    /**
     * Occurs when a message is completed.
     */
    THREAD_MESSAGE_COMPLETED("thread.message.completed", Message::class, Message.serializer()),

    /**
     * Occurs when a message ends before it is completed.
     */
    THREAD_MESSAGE_INCOMPLETE("thread.message.incomplete", Message::class, Message.serializer()),

    /**
     * Occurs when an error occurs. This can happen due to an internal server error or a timeout.
     */
    ERROR("error", String::class, String.serializer()),

    /**
     * Occurs when a stream ends.
     * data is [DONE]
     */
    DONE("done", String::class, String.serializer()),

    /**
     * Occurs when the event type is not recognized
     */
    UNKNOWN("unknown", String::class, String.serializer());

    public companion object {
        public fun fromEvent(event: String): AssistantStreamEventType =
            entries
                .find { it.event == event }
                ?: UNKNOWN
    }
}

/**
 * Custom serializer for [AssistantStreamEventType].
 */
@OptIn(BetaOpenAI::class)
public class AssistantStreamEventTypeSerializer : KSerializer<AssistantStreamEventType> {
    override val descriptor: SerialDescriptor = PrimitiveSerialDescriptor("AssistantStreamEventType", PrimitiveKind.STRING)

    override fun deserialize(decoder: Decoder): AssistantStreamEventType {
        val value = decoder.decodeString()
        return AssistantStreamEventType.entries.single { value == it.event }
    }
    override fun serialize(encoder: Encoder, value: AssistantStreamEventType) {
        encoder.encodeString(value.event)
    }
}
