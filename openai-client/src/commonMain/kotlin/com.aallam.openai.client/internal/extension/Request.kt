package com.aallam.openai.client.internal.extension

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.client.internal.JsonLenient
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.utils.io.core.readAvailable
import io.ktor.utils.io.core.writeFully
import kotlinx.io.buffered
import kotlinx.serialization.json.*

/**
 * Adds `stream` parameter to the request.
 */
internal fun CompletionRequest.toStreamRequest(): JsonElement {
    val json = JsonLenient.encodeToJsonElement(CompletionRequest.serializer(), this)
    return streamRequestOf(json)
}

internal inline fun <reified T> streamRequestOf(serializable: T): JsonElement {
    val enableStream = "stream" to JsonPrimitive(true)
    val json = JsonLenient.encodeToJsonElement(serializable)
    val map = json.jsonObject.toMutableMap().also { it += enableStream }
    return JsonObject(map)
}

internal fun FormBuilder.appendFileSource(key: String, fileSource: FileSource) {
    append(key = key, filename = fileSource.name, contentType = ContentType.parse(fileSource.contentType)) {
        fileSource.source.buffered().use { source ->
            val buffer = ByteArray(8192) // 8 KiB
            var bytesRead: Int
            while (source.readAvailable(buffer).also { bytesRead = it } != 0) {
                writeFully(buffer = buffer, offset = 0, length = bytesRead)
            }
        }
    }
}

internal fun HttpMessageBuilder.beta(api: String, version: Int) {
    header("OpenAI-Beta", "$api=v$version")
}
