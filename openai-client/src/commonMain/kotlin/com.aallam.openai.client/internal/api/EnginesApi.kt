package com.aallam.openai.client.internal.api

import com.aallam.openai.api.engine.Engine
import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.api.engine.EnginesResponse
import com.aallam.openai.client.Engines
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.request.get
import io.ktor.client.request.url
import io.ktor.http.ContentType
import io.ktor.http.contentType

/**
 * Implementation of [Engines].
 */
internal class EnginesApi(private val httpRequester: HttpTransport) : Engines {

    override suspend fun engines(): List<Engine> {
        return httpRequester.perform<EnginesResponse> { it.get { url(path = EnginesPath) } }.data
    }

    override suspend fun engine(engineId: EngineId): Engine {
        return httpRequester.perform {
            it.get {
                url(path = "$EnginesPath/$engineId")
                contentType(ContentType.Application.Json)
            }
        }
    }

    companion object {
        const val EnginesPath = "/v1/engines"
    }
}
