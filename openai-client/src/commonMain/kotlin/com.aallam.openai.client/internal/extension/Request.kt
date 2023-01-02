package com.aallam.openai.client.internal.extension

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.file.FilePath
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
import okio.*
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
internal fun FormBuilder.appendTextFile(fileSystem: FileSystem, key: String, filePath: FilePath) {
    val path = filePath.path.toPath()
    fileSystem.read(path) { appendTextFile(key, path.name, this) }
}

internal fun FormBuilder.appendTextFile(key: String, filename: String, source: Source) {
    append(key, filename, ContentType.Application.OctetStream) {
        source.buffer().use {
            while (true) {
                val line = it.readUtf8Line() ?: break
                appendLine(line)
            }
        }
    }
}

internal fun FormBuilder.appendBinaryFile(key: String, filename: String, source: Source) {
    source.buffer().use {
        val bytes = it.readByteArray()
        append(key, bytes, Headers.build {
            append(HttpHeaders.ContentType, ContentType.Application.OctetStream.toString())
            append(HttpHeaders.ContentDisposition, "filename=\"$filename\"")
        })
    }
}
