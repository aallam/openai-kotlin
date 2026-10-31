# Decisions

Use Decisions to evaluate evidence with predicate, choice or score questions. Answers arrive in question order, and any question can return a refusal. The API selects answers; your application performs any resulting actions.

```kotlin
import com.aallam.openai.api.decision.*
import com.aallam.openai.api.model.ModelId

val result = openAI.decision(decisionRequest {
    model = ModelId("gpt-6-luna")
    input = DecisionInput.Text("A red parcel marked urgent.")
    questions = listOf(
        DecisionQuestion.Predicate(
            instructions = DecisionContent.Text("Is the parcel urgent?"),
            name = "urgent",
        ),
        DecisionQuestion.Choice(
            instructions = DecisionContent.Text("Choose the parcel's color."),
            choices = listOf(
                DecisionChoice(DecisionValue.Text("red")),
                DecisionChoice(DecisionValue.Text("blue")),
            ),
            name = "color",
        ),
        DecisionQuestion.Score(
            instructions = DecisionContent.Text("Rate the delivery urgency."),
            levels = listOf(DecisionScoreLevel("routine"), DecisionScoreLevel("urgent")),
            name = "priority",
        ),
    )
})

for (answer in result.answers) {
    when (answer) {
        is DecisionAnswer.Predicate -> println("${answer.name}: ${answer.probability}")
        is DecisionAnswer.Choice -> println("${answer.name}: ${answer.choice}")
        is DecisionAnswer.Score -> println("${answer.name}: ${answer.score}")
        is DecisionAnswer.Refusal -> println("${answer.name}: refused")
    }
}
println(result.usage)
```

Choice values preserve their JSON types: `DecisionValue.Boolean(true)` differs from `DecisionValue.Text("true")`. Choice answers retain the selected value, confidence and per-option probabilities. Score answers retain the score, confidence and probabilities for the ordered levels.

## Images, JSON and conversation history

Use `DecisionInput.Items` for an ordered list of messages, function/custom tool calls and their outputs. `DecisionContent.Text` emits a JSON string; `DecisionContent.Parts` emits content blocks. Match each tool output's `callId` to its call.

```kotlin
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

val evidence = DecisionInput.Items(listOf(
    DecisionInputItem.Message(
        content = DecisionContent.Parts(listOf(
            DecisionContentPart.InputText("Evaluate this ticket."),
            DecisionContentPart.InputJson(buildJsonObject { put("urgent", true) }),
            DecisionContentPart.InputImage("https://example.com/parcel.jpg", detail = "low"),
        )),
    ),
))
```

Images use public HTTP(S) URLs or base64 data URLs. JSON evidence is an object or an array of objects. User messages support text/images/JSON; developer and system messages accept text; assistant messages accept output text/refusals. Tool outputs accept text/images. Question instructions accept text/images/JSON without input cache breakpoints. File IDs, audio and item references are not supported by Decisions.

## Caching and usage

`DecisionRequest` and its builder expose `promptCacheKey`, `promptCacheOptions` and `safetyIdentifier`. Input text/image blocks can carry a `DecisionPromptCacheBreakpoint`. Cache options control implicit versus explicit breakpoints and their TTL; the current TTL is `30m`. Cache fields are omitted when unset, leaving server defaults in effect.

`Decision.usage` includes input/output/total tokens, cached tokens, cache-write tokens and reasoning tokens. `RequestOptions` applies the same per-request headers and query parameters as other SDK operations. Custom client implementations can inherit the default unsupported Decisions method while adopting the new capability.

See the official [Decisions guide](https://developers.openai.com/api/docs/guides/decisions) and [create reference](https://developers.openai.com/api/reference/resources/decisions/methods/create) for model availability and current limits.
