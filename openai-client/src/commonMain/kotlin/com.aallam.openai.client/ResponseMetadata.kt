package com.aallam.openai.client

import com.aallam.openai.api.core.ResponseMetadata
import com.aallam.openai.client.internal.ResponseMetadataPlugin
import io.ktor.client.HttpClientConfig

/**
 * Observes HTTP status and response headers for every response received by this client, including
 * error responses, retries, and redirects, before decoding or streaming the body. A transport failure
 * or cancellation before receiving a response does not invoke [observer].
 *
 * The observer runs inline and should avoid blocking. Throwing from it fails the operation without
 * retrying the request; coroutine cancellation remains cancellation. Concurrent requests may invoke
 * the observer concurrently, so callers must coordinate any shared state.
 *
 * @param observer receives a snapshot of each response's status and headers.
 */
public fun HttpClientConfig<*>.onResponseMetadata(observer: (ResponseMetadata) -> Unit) {
    install(ResponseMetadataPlugin) {
        this.observer = observer
    }
}
