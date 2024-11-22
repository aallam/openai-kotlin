package com.aallam.openai.client.extension

import com.aallam.openai.api.run.AssistantStreamEvent
import com.aallam.openai.client.internal.JsonLenient
import kotlinx.serialization.KSerializer

/**
 * Get the data of the [AssistantStreamEvent] using the provided [serializer] from the corresponding event type.
 * @param <T> the type of the data.
 * @throws IllegalStateException if the [AssistantStreamEvent] data is null.
 * @throws ClassCastException if the [AssistantStreamEvent] data cannot be cast to the provided type.
 */
@Suppress("UNCHECKED_CAST")
public fun <T> AssistantStreamEvent.getData(): T {
	return type
		.let { it.serializer as? KSerializer<T> }
		?.let(::getData)
		?: throw IllegalStateException("Failed to decode ServerSentEvent: $rawType")
}


/**
 * Get the data of the [AssistantStreamEvent] using the provided [serializer].
 * @throws IllegalStateException if the [AssistantStreamEvent] data is null.
 * @throws ClassCastException if the [AssistantStreamEvent] data cannot be cast to the provided type.
 */
public fun <T> AssistantStreamEvent.getData(serializer: KSerializer<T>): T =
	data
		?.let { JsonLenient.decodeFromString(serializer, it) }
		?: throw IllegalStateException("ServerSentEvent data was null: $rawType")
