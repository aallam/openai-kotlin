package com.aallam.openai.api.thread

import com.aallam.openai.api.OpenAIDsl
import com.aallam.openai.api.core.Role
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

@Serializable
public data class ThreadRequest(
    /**
     * The role of the entity that is creating the message. Currently only [Role.User] is supported.
     */
    @SerialName("messages") public val messages: List<ThreadMessage>? = null,

    /**
     * Set of 16 key-value pairs that can be attached to an object.
     * This can be useful for storing additional information about the object in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maxium of 512 characters long.
     */
    @SerialName("metadata") public val metadata: Map<String, String>? = null,
)

/**
 * A thread request builder.
 */
@OpenAIDsl
public class ThreadRequestBuilder {

    /**
     * The list of messages to be included in the thread. Each message is represented by a [ThreadMessage].
     */
    public var messages: List<ThreadMessage>? = null

    /**
     * Sets a list of messages to the request.
     */
    public fun messages(block: ThreadMessagesBuilder.() -> Unit) {
        messages = ThreadMessagesBuilder().apply(block).build()
    }

    /**
     * Set of 16 key-value pairs that can be attached to the thread.
     * This can be useful for storing additional information about the thread in a structured format.
     * Keys can be a maximum of 64 characters long, and values can be a maximum of 512 characters long.
     */
    public var metadata: Map<String, String>? = null

    /**
     * Builds and returns a [ThreadRequest] instance.
     */
    public fun build(): ThreadRequest = ThreadRequest(
        messages = messages,
        metadata = metadata
    )
}

/**
 * Creates a [ThreadRequest] instance using the provided builder block.
 */
public fun threadRequest(block: ThreadRequestBuilder.() -> Unit): ThreadRequest =
    ThreadRequestBuilder().apply(block).build()

/**
 * A list of messages in a thread.
 */
@OpenAIDsl
public class ThreadMessagesBuilder {

    private val messages = mutableListOf<ThreadMessage>()

    /**
     * Adds a message to the list of messages.
     */
    public fun message(block: ThreadMessageBuilder.() -> Unit) {
        messages.add(ThreadMessageBuilder().apply(block).build())
    }

    /**
     * Builds and returns a list of [ThreadMessage] instances.
     */
    public fun build(): List<ThreadMessage> = messages
}
