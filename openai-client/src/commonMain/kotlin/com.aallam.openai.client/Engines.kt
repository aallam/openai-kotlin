package com.aallam.openai.client

import com.aallam.openai.api.engine.Engine
import com.aallam.openai.api.engine.EngineId


/**
 * OpenAI’s API provides access to several different engines - [Ada], [Babbage], [Curie] and [Davinci].
 */
public interface Engines {

    /**
     * Lists the currently available engines, and provides basic information about each one such as
     * the owner and availability.
     */
    public suspend fun engines(): List<Engine>

    /**
     * Retrieves an engine instance, providing basic information about the engine such as the owner
     * and availability.
     */
    public suspend fun engine(engineId: EngineId): Engine
}
