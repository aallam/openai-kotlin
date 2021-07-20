package com.aallam.openai.client.internal.api

import com.aallam.openai.api.engine.Engine
import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.api.engine.EnginesResponse
import com.aallam.openai.client.Engines
import io.ktor.client.*
import io.ktor.client.request.*

/**
 * Implementation of [Engines].
 */
internal class EnginesApi(private val httpClient: HttpClient) : Engines {

    override suspend fun engines(): List<Engine> {
        return httpClient.get<EnginesResponse>(path = EnginesPath).data
    }

    override suspend fun engine(engineId: EngineId): Engine {
        return httpClient.get(path = "$EnginesPath/$engineId")
    }

    companion object {
        const val EnginesPath = "/v1/engines"
    }
}