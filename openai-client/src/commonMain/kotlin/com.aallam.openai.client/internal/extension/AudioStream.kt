package com.aallam.openai.client.internal.extension

import com.aallam.openai.api.exception.OpenAIHttpException
import com.aallam.openai.client.internal.JsonLenient
import io.ktor.client.call.body
import io.ktor.client.statement.HttpResponse
import io.ktor.utils.io.ByteReadChannel
import io.ktor.utils.io.cancel
import io.ktor.utils.io.readUTF8Line
import kotlinx.coroutines.flow.FlowCollector
import kotlinx.serialization.json.*

/** Reads SSE records, including multiline data, without buffering the complete response. */
internal suspend inline fun <reified T> FlowCollector<T>.audioEventsFrom(response: HttpResponse) {
    val channel: ByteReadChannel = response.body()
    val data = StringBuilder()
    try {
        while (true) {
            val line = channel.readUTF8Line()
            if (line == null || line.isEmpty()) {
                if (data.isNotEmpty()) {
                    val payload = data.toString()
                    data.clear()
                    if (payload == "[DONE]") break
                    val json = JsonLenient.parseToJsonElement(payload).jsonObject
                    if (json["type"]?.jsonPrimitive?.content == "error" || json.containsKey("error")) {
                        val error = json["error"] as? JsonObject ?: json
                        throw OpenAIHttpException(IllegalStateException(error["message"]?.jsonPrimitive?.content ?: "Audio stream failed"))
                    }
                    emit(JsonLenient.decodeFromJsonElement<T>(json))
                }
                if (line == null) break
            } else if (line.startsWith("data:")) {
                if (data.isNotEmpty()) data.append('\n')
                data.append(line.removePrefix("data:").removePrefix(" "))
            }
        }
    } finally {
        channel.cancel()
    }
}
