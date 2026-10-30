package com.aallam.openai.api.core

import com.aallam.openai.api.http.Timeout

/**
 * Represents options for configuring a request to an endpoint.
 *
 * @property headers A mutable map of header names to their respective values to be sent with the request.
 * @property urlParameters A mutable map of URL parameter names to their respective values to be appended to the request URL.
 * @property timeout Http operations timeouts.
 * @property onResponse observes status and response headers before decoding or streaming the body,
 * including HTTP error responses. Called for each received response, including retries. It is not
 * called when no response is received. The callback runs inline and should avoid blocking;
 * throwing from it fails the operation, and coroutine cancellation remains cancellation.
 */
public data class RequestOptions(
    public val headers: Map<String, String> = emptyMap(),
    public val urlParameters: Map<String, String> = emptyMap(),
    public val timeout: Timeout? = null,
    public val onResponse: ((ResponseMetadata) -> Unit)? = null,
)
