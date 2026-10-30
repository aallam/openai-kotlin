package com.aallam.openai.client.internal

import com.aallam.openai.api.core.ResponseMetadata
import io.ktor.client.plugins.api.createClientPlugin
import io.ktor.util.AttributeKey
import kotlinx.coroutines.CancellationException

internal val responseObserverKey = AttributeKey<(ResponseMetadata) -> Unit>("OpenAIResponseObserver")

internal class ResponseObserverException(cause: Exception) : Exception("Response observer failed", cause)

internal val ResponseMetadataPlugin = createClientPlugin("OpenAIResponseMetadata") {
    onResponse { response ->
        val observer = response.call.request.attributes.getOrNull(responseObserverKey) ?: return@onResponse
        try {
            observer(ResponseMetadata(response.status.value, response.headers.entries().associate { it.key to it.value }))
        } catch (error: CancellationException) {
            throw error
        } catch (error: Exception) {
            throw ResponseObserverException(error)
        }
    }
}
