package com.aallam.openai.client.internal.extension

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.client.internal.JsonLenient
import io.ktor.client.request.forms.*
import io.ktor.http.ContentType
import io.ktor.utils.io.core.writeFully
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import okio.*

/**
 * Adds `stream` parameter to the request.
 * Tokens will be sent as data-only [server-sent events](https://developer.mozilla.org/en-US/docs/Web/API/Server-sent_events/Using_server-sent_events#Event_stream_format)
 * as they become available, with the stream terminated by a data: `[DONE]` message.
 */
internal fun CompletionRequest.toStreamRequest(): JsonElement {
    val enableStream = "stream" to JsonPrimitive(true)
    val jsonElement = JsonLenient.encodeToJsonElement(CompletionRequest.serializer(), this)
    val map = jsonElement.jsonObject.toMutableMap().also { it += enableStream }
    return JsonObject(map)
}


internal fun FormBuilder.appendFileSource(key: String, fileSource: FileSource) {
    append(key, fileSource.name, ContentType.Application.OctetStream) {
        fileSource.source.buffer().use { source ->
            val buffer = ByteArray(8192) // 8 KiB
            var bytesRead: Int
            while (source.read(buffer).also { bytesRead = it } != -1) {
                writeFully(src = buffer, offset = 0, length = bytesRead)
            }

        }
    }
}
