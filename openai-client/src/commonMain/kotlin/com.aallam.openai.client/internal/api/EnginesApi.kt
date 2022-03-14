package com.aallam.openai.client.internal.api

import com.aallam.openai.api.engine.Engine
import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.api.engine.EnginesResponse
import com.aallam.openai.client.Engines
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.url

/**
 * Implementation of [Engines].
 */
internal class EnginesApi(private val httpClient: HttpClient) : Engines {

    override suspend fun engines(): List<Engine> {
        return httpClient.get { url(path = EnginesPath) }.body<EnginesResponse>().data
    }

    override suspend fun engine(engineId: EngineId): Engine {
        return httpClient.get { url(path = "$EnginesPath/$engineId") }.body()
    }

    companion object {
        const val EnginesPath = "/v1/engines"
    }
}
