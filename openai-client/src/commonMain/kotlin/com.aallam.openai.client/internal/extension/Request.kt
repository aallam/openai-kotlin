package com.aallam.openai.client.internal.extension

import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.client.internal.JsonLenient
import io.ktor.client.request.*
import io.ktor.client.request.forms.*
import io.ktor.http.*
import io.ktor.utils.io.core.*
import kotlinx.serialization.json.*
import okio.buffer
import okio.use

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

internal fun HttpMessageBuilder.beta(api: String, version: Int) {
    header("OpenAI-Beta", "$api=v$version")
}
