package com.aallam.openai.api.engine

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable

/**
 * Basic information about an engine such as the owner and availability.
 */
@Serializable
public data class Engine(
    /**
     * An identifier for this engine, used to specify an engine for completions or searching.
     */
    @SerialName("id") public val id: EngineId,

    /**
     * The owner of the GPT-3 engine, typically "openai".
     */
    @SerialName("owner") public val owner: String,

    /**
     * Whether the engine is ready to process requests or not.
     */
    @SerialName("ready") public val ready: Boolean?
)
