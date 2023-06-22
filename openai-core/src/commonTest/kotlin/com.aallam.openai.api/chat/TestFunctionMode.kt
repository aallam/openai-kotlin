package com.aallam.openai.api.chat

import com.aallam.openai.api.BetaOpenAI
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

@OptIn(BetaOpenAI::class)
class TestFunctionMode {

    @Test
    fun serialize() {
        listOf(
            FunctionMode.Auto,
            FunctionMode.None,
            FunctionMode.Named("someFunctionName")
        ).forEach { functionMode ->
            val jsonString = Json.encodeToString(FunctionMode.serializer(), functionMode)
            val decoded = Json.decodeFromString(FunctionMode.serializer(), jsonString)
            assertEquals(functionMode, decoded)
        }
    }
}
