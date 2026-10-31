package com.aallam.openai.client.internal

import com.aallam.openai.api.core.ResponseMetadata
import io.ktor.client.plugins.api.createClientPlugin
import kotlinx.coroutines.CancellationException

internal class ResponseMetadataConfig {
    lateinit var observer: (ResponseMetadata) -> Unit
}

internal class ResponseObserverException(cause: Throwable) : Exception("Response observer failed", cause)

internal val ResponseMetadataPlugin = createClientPlugin("OpenAIResponseMetadata", ::ResponseMetadataConfig) {
    val observer = pluginConfig.observer
    onResponse { response ->
        try {
            observer(ResponseMetadata(response.status.value, response.headers.entries().associate { it.key to it.value }))
        } catch (error: CancellationException) {
            throw error
        } catch (error: Throwable) {
            throw ResponseObserverException(error)
        }
    }
}
