package com.aallam.openai.client

import com.aallam.openai.api.edits.Edit
import com.aallam.openai.api.edits.EditsRequest

/**
 * Given a prompt and an instruction, the model will return an edited version of the prompt.
 */
public interface Edits {

    /**
     * Creates a new edit for the provided input, instruction, and parameters.
     */
    @Deprecated("OpenAI retired the Edits endpoint on January 4, 2024. Use Responses or Chat Completions.")
    public suspend fun edit(request: EditsRequest): Edit
}
