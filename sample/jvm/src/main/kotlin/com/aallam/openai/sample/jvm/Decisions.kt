package com.aallam.openai.sample.jvm

import com.aallam.openai.api.decision.*
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.OpenAI

suspend fun decisions(openAI: OpenAI) {
    val result = openAI.decision(decisionRequest {
        model = ModelId("gpt-6-luna")
        input = DecisionInput.Text("A red parcel marked urgent.")
        questions = listOf(
            DecisionQuestion.Predicate(DecisionContent.Text("Is the parcel urgent?"), "urgent"),
            DecisionQuestion.Choice(DecisionContent.Text("Choose the parcel's color."), listOf(
                DecisionChoice(DecisionValue.Text("red")), DecisionChoice(DecisionValue.Text("blue")),
            ), "color"),
            DecisionQuestion.Score(DecisionContent.Text("Rate delivery urgency."), listOf(
                DecisionScoreLevel("routine"), DecisionScoreLevel("urgent"),
            ), "priority"),
        )
    })
    result.answers.forEach { answer ->
        when (answer) {
            is DecisionAnswer.Predicate -> println("${answer.name}: ${answer.probability}")
            is DecisionAnswer.Choice -> println("${answer.name}: ${answer.choice}")
            is DecisionAnswer.Score -> println("${answer.name}: ${answer.score}")
            is DecisionAnswer.Refusal -> println("${answer.name}: refused")
        }
    }
}
