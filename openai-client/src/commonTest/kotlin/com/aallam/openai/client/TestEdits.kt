package com.aallam.openai.client

import com.aallam.openai.api.edits.editsRequest
import com.aallam.openai.api.model.ModelId
import kotlin.test.Test
import kotlin.test.assertTrue

class TestEdits : TestOpenAI() {

    @Test
    fun edits() = test {
        val response = openAI.edit(
            request = editsRequest {
                model = ModelId("text-davinci-edit-001")
                input = "What day of the wek is it?"
                instruction = "Fix the spelling mistakes"
            }
        )
        assertTrue { response.created != 0L }
        assertTrue { response.choices.isNotEmpty() }
    }
}
