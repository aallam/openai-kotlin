package com.aallam.openai.client.internal.extension

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.client.internal.JsonLenient
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject

/**
 * Adds `stream` parameter to the request.
 * Tokens will be sent as data-only [server-sent events](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#Event_stream_format)
 * as they become available, with the stream terminated by a data: `[DONE]` message.
 */
internal fun CompletionRequest?.toStreamRequest(): JsonElement {
    val enableStream = "stream" to JsonPrimitive(true)
    return when (this) {
        null -> JsonObject(mapOf(enableStream))
        else -> {
            val jsonElement = JsonLenient.encodeToJsonElement(CompletionRequest.serializer(), this)
            val map = jsonElement.jsonObject.toMutableMap().also { it += enableStream }
            JsonObject(map)
        }
    }
}
