package com.aallam.openai.client.extension

import com.aallam.openai.api.run.AssistantStreamEvent
import com.aallam.openai.client.internal.JsonLenient
import kotlinx.serialization.KSerializer


@Suppress("UNCHECKED_CAST")
public fun <T> AssistantStreamEvent.getValue(): T {
	return JsonLenient.decodeFromString(event.serializer as KSerializer<T>, data)
}