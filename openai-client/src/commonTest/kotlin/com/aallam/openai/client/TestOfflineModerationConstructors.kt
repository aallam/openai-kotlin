package com.aallam.openai.client

import com.aallam.openai.api.moderation.Categories
import com.aallam.openai.api.moderation.CategoryScores
import com.aallam.openai.api.moderation.ModerationResult
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertNull

class TestOfflineModerationConstructors {
    @Test
    fun inferredLegacyConstructorsKeepTheirArity() {
        val makeCategories = ::Categories
        val categories = makeCategories(false, false, false, false, false, false, false, false, false, false, false)
        val makeScores = ::CategoryScores
        val scores = makeScores(0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0, 0.0)
        val makeResult = ::ModerationResult
        val result = makeResult(categories, scores, false)
        assertFalse(result.flagged)
        assertNull(result.categoryAppliedInputTypes)
        assertNull(result.categories.illicit)
        assertNull(result.categoryScores.illicit)
    }

}
