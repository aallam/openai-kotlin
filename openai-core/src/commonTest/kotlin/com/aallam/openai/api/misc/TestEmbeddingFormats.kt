package com.aallam.openai.api.misc

import com.aallam.openai.api.embedding.*
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlin.test.*

class TestEmbeddingFormats {
    @Test
    fun allInputVariantsUseTheirUnwrappedApiShapes() {
        val cases = listOf(
            EmbeddingInput.Text("hello") to "\"hello\"",
            EmbeddingInput.Texts(listOf("hello", "world")) to "[\"hello\",\"world\"]",
            EmbeddingInput.Tokens(listOf(12, 34)) to "[12,34]",
            EmbeddingInput.TokenBatch(listOf(listOf(12), listOf(34, 56))) to "[[12],[34,56]]",
        )
        for ((input, expected) in cases) {
            val request = EmbeddingCreateRequest(ModelId("model"), input, dimensions = 4, user = "user", encodingFormat = EmbeddingEncodingFormat.Base64)
            val wire = Json.encodeToJsonElement(request).jsonObject
            assertEquals(Json.parseToJsonElement(expected), wire["input"])
            assertEquals("base64", wire["encoding_format"]?.jsonPrimitive?.content)
            assertEquals(input, Json.decodeFromString<EmbeddingCreateRequest>(Json.encodeToString(request)).input)
        }
    }

    @Test
    fun decodesIndependentLittleEndianFloat32FixtureIncludingNegativeZero() {
        // Python struct.pack('<ffff', 1, -2.5, 0, -0.0), base64 encoded.
        val vector = Json.decodeFromString<EmbeddingVector>("\"AACAPwAAIMAAAAAAAAAAgA==\"")
        val values = vector.values()
        assertEquals(listOf(1.0, -2.5, 0.0, -0.0), values)
        assertEquals((-0.0).toRawBits(), values.last().toRawBits())
        assertEquals("AACAPwAAIMAAAAAAAAAAgA==", assertIs<EmbeddingVector.Encoded>(vector).value)
    }

    @Test
    fun numericVectorsRetainDoublePrecisionAndMalformedEncodedDataFails() {
        val numeric = Json.decodeFromString<EmbeddingVector>("[0.3333333333333333,-2.5]")
        assertEquals(listOf(1.0 / 3.0, -2.5), numeric.values())
        assertFailsWith<IllegalArgumentException> { EmbeddingVector.Encoded("AA==").values() }
        assertFailsWith<IllegalArgumentException> { EmbeddingVector.Encoded("!!!").values() }
        assertFailsWith<IllegalArgumentException> { EmbeddingInput.TokenBatch(listOf(emptyList())) }
    }

    @Test
    fun legacyEmbeddingPropertiesAndBuildersKeepTheirTypes() {
        val request = embeddingRequest { model = ModelId("model"); input = listOf("hello"); dimensions = 2 }
        val input: List<String> = request.input
        val legacy = Embedding(listOf(1.0, 2.0), 0)
        val coordinates: List<Double> = legacy.embedding
        assertEquals("hello", input.single())
        assertEquals(2.0, coordinates.last())
    }
}
