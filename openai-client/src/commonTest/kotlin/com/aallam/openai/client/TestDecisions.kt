package com.aallam.openai.client

import com.aallam.openai.api.decision.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.internal.reportDecisionUsage
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put
import kotlin.test.*

class TestDecisions : TestOpenAI() {
    @Test
    fun evaluatesPredicateChoiceAndScoreFromJsonEvidence() = test {
        val result = openAI.decision(decisionRequest {
            model = ModelId("gpt-6-luna")
            input = DecisionInput.Items(listOf(DecisionInputItem.Message(
                content = DecisionContent.Parts(listOf(DecisionContentPart.InputJson(buildJsonObject {
                    put("color", "red")
                    put("urgent", true)
                }))),
            )))
            questions = listOf(
                DecisionQuestion.Predicate(DecisionContent.Text("Is urgent true?"), "urgent"),
                DecisionQuestion.Choice(DecisionContent.Text("Select the color."), listOf(
                    DecisionChoice(DecisionValue.Text("red")), DecisionChoice(DecisionValue.Text("blue")),
                ), "color"),
                DecisionQuestion.Score(DecisionContent.Text("Rate urgency based on urgent."), listOf(
                    DecisionScoreLevel("routine", "urgent is false"), DecisionScoreLevel("urgent", "urgent is true"),
                ), "priority"),
            )
        })
        reportDecisionUsage(result.model, result.usage)
        assertEquals(listOf("urgent", "color", "priority"), result.answers.map { it.name })
        val predicate = assertIs<DecisionAnswer.Predicate>(result.answers[0])
        assertTrue(predicate.probability in 0.5..1.0)
        val choice = assertIs<DecisionAnswer.Choice>(result.answers[1])
        assertEquals(DecisionValue.Text("red"), choice.choice)
        assertTrue(choice.confidence in 0.0..1.0)
        assertEquals(2, choice.probabilities.size)
        val score = assertIs<DecisionAnswer.Score>(result.answers[2])
        assertTrue(score.score in 0.5..1.0)
        assertEquals(listOf("routine", "urgent"), score.probabilities.map { it.label })
        assertTrue(result.usage.totalTokens > 0)
        assertEquals(result.usage.inputTokens + result.usage.outputTokens, result.usage.totalTokens)
    }
}
