package com.aallam.openai.api.run

import com.aallam.openai.api.BetaOpenAI
import com.aallam.openai.api.core.Parameters
import com.aallam.openai.api.core.Role
import com.aallam.openai.api.message.Message
import com.aallam.openai.api.message.MessageContent
import com.aallam.openai.api.message.MessageId
import com.aallam.openai.api.thread.Thread
import kotlinx.serialization.KSerializer
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.descriptors.SerialDescriptor
import kotlinx.serialization.descriptors.buildClassSerialDescriptor
import kotlinx.serialization.descriptors.element
import kotlinx.serialization.encoding.Decoder
import kotlinx.serialization.encoding.Encoder
import kotlinx.serialization.json.JsonDecoder
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonEncoder
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonObject
import kotlinx.serialization.json.jsonPrimitive
import kotlin.reflect.KClass

@BetaOpenAI
@Serializable
public data class AssistantStreamEvent(
	@SerialName("event") val event: AssistantStreamEventType,
	@SerialName("data") val data: String
)

@BetaOpenAI
@Serializable
public data class RunStepDelta(
    @SerialName("id") val id: RunStepId,
    @SerialName("object") val `object`: String,
    @SerialName("delta") val delta: RunStepDeltaData
)

@BetaOpenAI
@Serializable
public data class RunStepDeltaData(
    @SerialName("step_details") val stepDetails: RunStepDetails
)

@BetaOpenAI
@Serializable
public data class MessageDelta(
    @SerialName("id") val id: MessageId,
    @SerialName("object") val `object`: String,
    @SerialName("delta") val delta: MessageDeltaData
)

@BetaOpenAI
@Serializable
public data class MessageDeltaData(
    @SerialName("role") val role: Role,
    @SerialName("content") val content: MessageContent
)

@BetaOpenAI
@Serializable
public enum class AssistantStreamEventType(public val dataType: KClass<*>, public val serializer: KSerializer<*>) {

    /**
     * Occurs when a new thread is created.
     */
    @SerialName("thread.created")
    THREAD_CREATED(Thread::class, Thread.serializer()),

    /**
     * Occurs when a new run is created.
     */
    @SerialName("thread.run.created")
    THREAD_RUN_CREATED(Run::class, Run.serializer()),

    /**
     * Occurs when a run moves to a queued status.
     */
    @SerialName("thread.run.queued")
    THREAD_RUN_QUEUED(Run::class, Run.serializer()),

    /**
     * Occurs when a run moves to an in_progress status.
     */
    @SerialName("thread.run.in_progress")
    THREAD_RUN_IN_PROGRESS(Run::class, Run.serializer()),

    /**
     * Occurs when a run moves to a requires_action status.
     */
    @SerialName("thread.run.requires_action")
    THREAD_RUN_REQUIRES_ACTION(Run::class, Run.serializer()),

    /**
     * Occurs when a run is completed.
     */
    @SerialName("thread.run.completed")
    THREAD_RUN_COMPLETED(Run::class, Run.serializer()),

    /**
     * Occurs when a run ends with status incomplete.
     */
    @SerialName("thread.run.incomplete")
    THREAD_RUN_INCOMPLETE(Run::class, Run.serializer()),

    /**
     * Occurs when a run fails.
     */
    @SerialName("thread.run.failed")
    THREAD_RUN_FAILED(Run::class, Run.serializer()),

    /**
     * Occurs when a run moves to a cancelling status.
     */
    @SerialName("thread.run.cancelling")
    THREAD_RUN_CANCELLING(Run::class, Run.serializer()),

    /**
     * Occurs when a run is cancelled.
     */
    @SerialName("thread.run.cancelled")
    THREAD_RUN_CANCELLED(Run::class, Run.serializer()),

    /**
     * Occurs when a run expires.
     */
    @SerialName("thread.run.expired")
    THREAD_RUN_EXPIRED(Run::class, Run.serializer()),

    /**
     * Occurs when a run step is created.
     */
    @SerialName("thread.run.step.created")
    THREAD_RUN_STEP_CREATED(RunStep::class, RunStep.serializer()),

    /**
     * Occurs when a run step moves to an in_progress state.
     */
    @SerialName("thread.run.step.in_progress")
    THREAD_RUN_STEP_IN_PROGRESS(RunStep::class, RunStep.serializer()),

    /**
     * Occurs when parts of a run step are being streamed.
     */
    @SerialName("thread.run.step.delta")
    THREAD_RUN_STEP_DELTA(RunStepDelta::class, RunStepDelta.serializer()),

    /**
     * Occurs when a run step is completed.
     */
    @SerialName("thread.run.step.completed")
    THREAD_RUN_STEP_COMPLETED(RunStep::class, RunStep.serializer()),

    /**
     * Occurs when a run step fails.
     */
    @SerialName("thread.run.step.failed")
    THREAD_RUN_STEP_FAILED(RunStep::class, RunStep.serializer()),

    /**
     * Occurs when a run step is cancelled.
     */
    @SerialName("thread.run.step.cancelled")
    THREAD_RUN_STEP_CANCELLED(RunStep::class, RunStep.serializer()),

    /**
     * Occurs when a run step expires.
     */
    @SerialName("thread.run.step.expired")
    THREAD_RUN_STEP_EXPIRED(RunStep::class, RunStep.serializer()),

    /**
     * Occurs when a message is created.
     */
    @SerialName("thread.message.created")
    THREAD_MESSAGE_CREATED(Message::class, RunStep.serializer()),

    /**
     * Occurs when a message moves to an in_progress state.
     */
    @SerialName("thread.message.in_progress")
    THREAD_MESSAGE_IN_PROGRESS(Message::class, Message.serializer()),

    /**
     * Occurs when parts of a Message are being streamed.
     */
    @SerialName("thread.message.delta")
    THREAD_MESSAGE_DELTA(MessageDelta::class, MessageDelta.serializer()),

    /**
     * Occurs when a message is completed.
     */
    @SerialName("thread.message.completed")
    THREAD_MESSAGE_COMPLETED(Message::class, Message.serializer()),

    /**
     * Occurs when a message ends before it is completed.
     */
    @SerialName("thread.message.incomplete")
    THREAD_MESSAGE_INCOMPLETE(Message::class, Message.serializer()),

    /**
     * Occurs when an error occurs. This can happen due to an internal server error or a timeout.
     */
    @SerialName("error")
    ERROR(String::class, String.serializer()),

    /**
     * Occurs when a stream ends.
     * data is [DONE]
     */
    @SerialName("done")
    DONE(String::class, String.serializer())
}