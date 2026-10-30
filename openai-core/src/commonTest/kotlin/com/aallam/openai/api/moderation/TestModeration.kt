package com.aallam.openai.api.moderation

import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertFalse
import kotlin.test.assertNull
import kotlin.test.assertTrue

class TestModeration {
    @Test
    fun multimodalInputUsesContentDiscriminatorsAndNestedImageUrl() {
        val request = MultimodalModerationRequest(
            input = listOf(
                ModerationInput.Text("Check this image"),
                ModerationInput.Image("data:image/png;base64,aGVsbG8="),
            ),
            model = ModerationModel.OmniLatest,
        )
        val expected = Json.parseToJsonElement(
            """{"input":[{"type":"text","text":"Check this image"},{"type":"image_url","image_url":{"url":"data:image/png;base64,aGVsbG8="}}],"model":"omni-moderation-latest"}"""
        )
        assertEquals(expected, Json.parseToJsonElement(Json.encodeToString(request)))
        val decoded = Json.decodeFromString<MultimodalModerationRequest>(expected.toString())
        assertEquals(request, decoded)
    }

    @Test
    fun textBuilderKeepsStringArrayAndOmitsDefaultModel() {
        val request = moderationRequest { input = listOf("hello", "world") }
        assertNull(request.model)
        assertEquals(
            Json.parseToJsonElement("""{"input":["hello","world"]}"""),
            Json.parseToJsonElement(Json.encodeToString(request)),
        )
    }

    @Test
    fun multimodalRequestAlsoLeavesModelSelectionToServer() {
        val request = MultimodalModerationRequest(listOf(ModerationInput.Text("hello")))
        assertEquals(
            Json.parseToJsonElement("""{"input":[{"type":"text","text":"hello"}]}"""),
            Json.parseToJsonElement(Json.encodeToString(request)),
        )
    }

    @Test
    fun omniResultsRetainIllicitCategoriesAndAppliedModalities() {
        val result = Json.decodeFromString<ModerationResult>(
            """{
                "flagged":true,
                "categories":$categories,
                "category_scores":$scores,
                "category_applied_input_types":{"violence":["text","image"],"illicit":["text"]}
            }"""
        )
        assertTrue(result.flagged)
        assertTrue(result.categories.illicit == true)
        assertFalse(result.categories.illicitViolent == true)
        assertEquals(0.9, result.categoryScores.illicit)
        assertEquals(0.1, result.categoryScores.illicitViolent)
        assertEquals(listOf("text", "image"), result.categoryAppliedInputTypes?.get("violence"))
    }

    @Test
    fun legacyResultStillDecodesWithoutOmniFields() {
        val result = Json.decodeFromString<ModerationResult>(
            """{"flagged":false,"categories":${categories.replace(",\"illicit\":true,\"illicit/violent\":false", "")},"category_scores":${scores.replace(",\"illicit\":0.9,\"illicit/violent\":0.1", "")}}"""
        )
        assertNull(result.categories.illicit)
        assertNull(result.categories.illicitViolent)
        assertNull(result.categoryScores.illicit)
        assertNull(result.categoryScores.illicitViolent)
        assertNull(result.categoryAppliedInputTypes)
    }

    @Test
    fun unsupportedIllicitFlagsMayBeNull() {
        val decoded = Json.decodeFromString<Categories>(
            categories.replace("\"illicit\":true", "\"illicit\":null")
        )
        assertNull(decoded.illicit)
    }

    private val categories = """{"hate":false,"hate/threatening":false,"self-harm":false,"sexual":false,"sexual/minors":false,"violence":false,"violence/graphic":false,"harassment":false,"harassment/threatening":false,"self-harm/intent":false,"self-harm/instructions":false,"illicit":true,"illicit/violent":false}"""
    private val scores = """{"hate":0.0,"hate/threatening":0.0,"self-harm":0.0,"sexual":0.0,"sexual/minors":0.0,"violence":0.0,"violence/graphic":0.0,"harassment":0.0,"harassment/threatening":0.0,"self-harm/intent":0.0,"self-harm/instructions":0.0,"illicit":0.9,"illicit/violent":0.1}"""
}
