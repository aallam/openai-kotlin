package com.aallam.openai.client.misc

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.client.OpenAI
import com.aallam.openai.client.TestOpenAI
import com.aallam.openai.client.token
import io.ktor.client.request.*
import kotlin.test.Test
import kotlin.test.assertEquals

class TestRequestOptions : TestOpenAI() {

    @Test
    fun test() = test {
        val key = "key"
        val requestOptions = RequestOptions(
            headers = mapOf(key to "valueB"),
        )
        var requestHeaders: Map<String, String>? = null
        val client = OpenAI(
            token = token,
            headers = mapOf(key to "valueA"),
            httpClientConfig = {
                install("RequestInterceptor") {
                    requestPipeline.intercept(HttpRequestPipeline.Before) {
                        requestHeaders = context.headers.entries().associate { it.key to it.value.first() }
                    }
                }
            }
        )

        client.models(requestOptions = requestOptions)
        assertEquals(requestHeaders?.get(key), requestOptions.headers[key])
    }

    @Test
    fun testOverride() = test {
        val key = "OpenAI-Beta"
        val requestOptions = RequestOptions(
            headers = mapOf(key to "assistants=v0"),
        )
        var requestHeaders: Map<String, String>? = null
        val client = OpenAI(
            token = token,
            headers = mapOf(key to "assistants=v3"),
            httpClientConfig = {
                install("RequestInterceptor") {
                    requestPipeline.intercept(HttpRequestPipeline.Before) {
                        requestHeaders = context.headers.entries().associate { it.key to it.value.first() }
                    }
                }
            }
        )

        try {
            client.assistants(requestOptions = requestOptions)
        } catch (e: Exception) {
            // skip
        }
        assertEquals(requestHeaders?.get(key), requestOptions.headers[key])
    }
}
