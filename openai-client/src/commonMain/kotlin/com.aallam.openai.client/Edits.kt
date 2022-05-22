package com.aallam.openai.client

import com.aallam.openai.api.edits.EditsRequest
import com.aallam.openai.api.edits.EditsResponse
import com.aallam.openai.api.engine.EngineId

/**
 * Given a prompt and an instruction, the model will return an edited version of the prompt.
 */
public interface Edits {

    /**
     * Creates a new edit for the provided input, instruction, and parameters.
     */
    public suspend fun edit(engineId: EngineId, request: EditsRequest): EditsResponse
}
