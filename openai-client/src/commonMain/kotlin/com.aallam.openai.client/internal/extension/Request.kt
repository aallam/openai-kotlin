package com.aallam.openai.client.internal.extension

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.client.internal.JsonLenient
import io.ktor.client.request.forms.FormBuilder
import io.ktor.client.request.forms.append
import io.ktor.http.ContentType
import io.ktor.http.Headers
import io.ktor.http.HttpHeaders
import kotlinx.serialization.json.JsonElement
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.jsonObject
import okio.FileSystem
import okio.Path.Companion.toPath

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

/**
 * Appends file content to the given FormBuilder.
 */
internal fun FormBuilder.appendTextFile(fileSystem: FileSystem, key: String, filePath: String) {
    val path = filePath.toPath()
    append(key, path.name, ContentType.Application.OctetStream) {
        fileSystem.read(path) {
            while (true) {
                val line = readUtf8Line() ?: break
                appendLine(line)
            }
        }
    }
}

internal fun FormBuilder.appendBinaryFile(fileSystem: FileSystem, key: String, filePath: String) {
    val path = filePath.toPath()
    fileSystem.read(path) {
        val bytes = readByteArray()
        append(key, bytes, Headers.build {
            append(HttpHeaders.ContentType, ContentType.Application.OctetStream.toString())
            append(HttpHeaders.ContentDisposition, "filename=\"${path.name}\"")
        })
    }
}
