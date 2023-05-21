package com.aallam.openai.client

import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestModels : TestOpenAI() {

    @Test
    fun models() = test {
        val resModels = openAI.models()
        assertTrue { resModels.isNotEmpty() }
        val resModel = resModels.first()
        val model = openAI.model(resModel.id)
        assertEquals(resModel, model)
    }
}
