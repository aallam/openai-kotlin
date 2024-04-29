package com.aallam.openai.api.batch

import com.aallam.openai.api.core.RequestId
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

/**
 * The response output of the API request.
 */
@Serializable
public data class ResponseOutput(
    /**
     * The HTTP status code of the response
     */
    @SerialName("status") public val status: Int,

    /**
     * An unique identifier for the OpenAI API request. Please include this request ID when contacting support.
     */
    @SerialName("request_id") public val requestId: RequestId,

    /**
     * The JSON body of the response
     */
    @SerialName("body") public val body: JsonObject,
)
