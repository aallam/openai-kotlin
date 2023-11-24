package com.aallam.openai.api.misc

import com.aallam.openai.api.chat.FunctionMode
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

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
