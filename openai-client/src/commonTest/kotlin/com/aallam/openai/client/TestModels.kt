package com.aallam.openai.client

import kotlinx.coroutines.test.runTest
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class TestModels : TestOpenAI() {

    @Test
    fun models() = runTest {
        val resModels = openAI.models()
        assertTrue { resModels.isNotEmpty() }
        val resModel = resModels.first()
        val model = openAI.model(resModel.id)
        assertEquals(resModel, model)
    }
}
