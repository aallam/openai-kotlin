package com.aallam.openai.client.extension

import com.aallam.openai.api.run.AssistantStreamEvent
import com.aallam.openai.api.run.AssistantStreamEventType
import com.aallam.openai.client.internal.JsonLenient
import io.ktor.sse.ServerSentEvent
import kotlinx.serialization.KSerializer

/**
 * Convert a [ServerSentEvent] to [AssistantStreamEvent]. Type will be [AssistantStreamEventType.UNKNOWN] if the event is null or unrecognized.
 */
internal fun ServerSentEvent.toAssistantStreamEvent() : AssistantStreamEvent =
	AssistantStreamEvent(
		event,
		event
			?.let(AssistantStreamEventType::fromEvent)
			?:AssistantStreamEventType.UNKNOWN,
		data
	)
