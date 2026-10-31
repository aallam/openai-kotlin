package com.aallam.openai.api.misc

import com.aallam.openai.api.decision.*
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlin.test.*

class TestDecisions {
    @Test
    fun plainTextUsesPrimitiveInputAndQuestionDiscriminatorWithoutOptionalFields() {
        val request = decisionRequest {
            model = ModelId("gpt-6-luna")
            input = DecisionInput.Text("The screen is broken.")
            questions = listOf(DecisionQuestion.Predicate(DecisionContent.Text("Is the item damaged?")))
        }
        val actual = Json.encodeToJsonElement(request)
        assertEquals(Json.parseToJsonElement("""{
            "model":"gpt-6-luna","input":"The screen is broken.",
            "questions":[{"type":"predicate","instructions":"Is the item damaged?"}]
        }"""), actual)
        assertEquals(request, Json.decodeFromJsonElement<DecisionRequest>(actual))
    }

    @Test
    fun orderedConversationRetainsEveryMessageAndToolVariant() {
        val image = DecisionContentPart.InputImage("https://example.com/image.png", "original")
        val input = DecisionInput.Items(listOf(
            DecisionInputItem.Message(DecisionContent.Text("Follow the rubric."), DecisionRole.System),
            DecisionInputItem.Message(DecisionContent.Parts(listOf(DecisionContentPart.InputText("Use the evidence."))), DecisionRole.Developer),
            DecisionInputItem.Message(DecisionContent.Parts(listOf(
                DecisionContentPart.InputText("Compare this delivery."), image,
                DecisionContentPart.InputJson(buildJsonObject { put("damaged", true) }),
            ))),
            DecisionInputItem.Message(DecisionContent.Parts(listOf(
                DecisionContentPart.OutputText("I checked it."), DecisionContentPart.Refusal("Earlier refusal."),
            )), DecisionRole.Assistant, phase = "final_answer"),
            DecisionInputItem.FunctionCall("{}", "call_1", "lookup", "item_1", "warehouse", "completed"),
            DecisionInputItem.FunctionCallOutput("call_1", DecisionContent.Parts(listOf(image)), "item_2", "completed"),
            DecisionInputItem.CustomToolCall("call_2", "order 42", "lookup_text", "item_3", "warehouse", "completed"),
            DecisionInputItem.CustomToolCallOutput("call_2", DecisionContent.Text("Delivered."), "item_4", "completed"),
        ))
        val encoded = Json.encodeToJsonElement<DecisionInput>(input).jsonArray
        assertEquals(listOf("message", "message", "message", "message", "function_call", "function_call_output", "custom_tool_call", "custom_tool_call_output"),
            encoded.map { it.jsonObject.getValue("type").jsonPrimitive.content })
        assertEquals("user", encoded[2].jsonObject.getValue("role").jsonPrimitive.content)
        assertEquals("final_answer", encoded[3].jsonObject.getValue("phase").jsonPrimitive.content)
        assertEquals("call_1", encoded[5].jsonObject.getValue("call_id").jsonPrimitive.content)
        assertEquals("Delivered.", encoded[7].jsonObject.getValue("output").jsonPrimitive.content)
        assertEquals(input, Json.decodeFromJsonElement<DecisionInput>(encoded))
        assertEquals(DecisionInput.Items(listOf(DecisionInputItem.Message(DecisionContent.Text("hello")))),
            Json.decodeFromString<DecisionInput>("""[{"role":"user","content":"hello"}]"""))
    }

    @Test
    fun multimodalQuestionsCacheControlsAndBuilderFieldsMatchTheWireContract() {
        val input = DecisionInput.Items(listOf(DecisionInputItem.Message(DecisionContent.Parts(listOf(
            DecisionContentPart.InputText("Reusable prefix", DecisionPromptCacheBreakpoint()),
            DecisionContentPart.InputImage("data:image/png;base64,AQ==", "low", DecisionPromptCacheBreakpoint()),
        )))))
        val instructions = DecisionContent.Parts(listOf(
            DecisionContentPart.InputText("Compare evidence with this reference."),
            DecisionContentPart.InputImage("https://example.com/reference.png"),
            DecisionContentPart.InputJson(buildJsonArray { add(buildJsonObject { put("reference", true) }) }),
        ))
        val questions = listOf(
            DecisionQuestion.Predicate(instructions, "matches"),
            DecisionQuestion.Choice(DecisionContent.Text("Pick a typed value."), listOf(
                DecisionChoice(DecisionValue.Text("true"), "The literal text."),
                DecisionChoice(DecisionValue.Boolean(true), "The boolean."),
            ), "category"),
            DecisionQuestion.Score(DecisionContent.Text("Rate damage."), listOf(
                DecisionScoreLevel("none", "No damage."), DecisionScoreLevel("severe", "Significant damage."),
            ), "severity"),
        )
        val direct = DecisionRequest(ModelId("gpt-6-luna"), input, questions, "orders", DecisionPromptCacheOptions("explicit", "30m"), "user-42")
        val built = decisionRequest {
            model = direct.model
            this.input = direct.input
            this.questions = direct.questions
            promptCacheKey = direct.promptCacheKey
            promptCacheOptions = direct.promptCacheOptions
            safetyIdentifier = direct.safetyIdentifier
        }
        assertEquals(direct, built)
        val body = Json.encodeToJsonElement(built).jsonObject
        assertEquals(Json.parseToJsonElement("""{"mode":"explicit","ttl":"30m"}"""), body["prompt_cache_options"])
        assertEquals(JsonPrimitive("orders"), body["prompt_cache_key"])
        assertEquals(JsonPrimitive("user-42"), body["safety_identifier"])
        val parts = body.getValue("input").jsonArray.single().jsonObject.getValue("content").jsonArray
        assertEquals(Json.parseToJsonElement("""{"mode":"explicit"}"""), parts.first().jsonObject["prompt_cache_breakpoint"])
        assertEquals(Json.parseToJsonElement("""{"mode":"explicit"}"""), parts.last().jsonObject["prompt_cache_breakpoint"])
        val encodedChoices = body.getValue("questions").jsonArray[1].jsonObject.getValue("choices").jsonArray
        assertEquals(JsonPrimitive("true"), encodedChoices[0].jsonObject["value"])
        assertEquals(JsonPrimitive(true), encodedChoices[1].jsonObject["value"])
        assertEquals(direct, Json.decodeFromJsonElement<DecisionRequest>(body))
    }

    @Test
    fun answersDecodeRefusalsTypedChoicesFractionalScoresAndCompleteUsage() {
        val result = Json.decodeFromString<Decision>("""{
            "model":"gpt-6-luna",
            "answers":[
                {"type":"predicate","name":"damaged","probability":0.9},
                {"type":"choice","name":null,"choice":true,"confidence":0.8,"probabilities":[
                    {"value":"true","probability":0.2},{"value":true,"probability":0.8}]},
                {"type":"score","name":"severity","score":0.75,"confidence":0.9,"probabilities":[
                    {"label":"high","value":3000000000,"probability":0.75}]},
                {"type":"refusal","name":null}
            ],
            "usage":{"input_tokens":42,"input_tokens_details":{"cached_tokens":12,"cache_write_tokens":5},
                "output_tokens":3,"output_tokens_details":{"reasoning_tokens":2},"total_tokens":45}
        }""")
        assertEquals(0.9, assertIs<DecisionAnswer.Predicate>(result.answers[0]).probability)
        val choice = assertIs<DecisionAnswer.Choice>(result.answers[1])
        assertEquals(DecisionValue.Boolean(true), choice.choice)
        assertEquals(listOf(DecisionValue.Text("true"), DecisionValue.Boolean(true)), choice.probabilities.map { it.value })
        val score = assertIs<DecisionAnswer.Score>(result.answers[2])
        assertEquals(0.75, score.score)
        assertEquals(3000000000L, score.probabilities.single().value)
        assertNull(assertIs<DecisionAnswer.Refusal>(result.answers[3]).name)
        assertEquals(DecisionUsage(42, DecisionInputTokensDetails(12, 5), 3, DecisionOutputTokensDetails(2), 45), result.usage)
        assertEquals(result, Json.decodeFromString<Decision>(Json.encodeToString(result)))
    }

    @Test
    fun contextsRejectUnsupportedContentWithoutCoercingValues() {
        val image = DecisionContent.Parts(listOf(DecisionContentPart.InputImage("https://example.com/image.png")))
        assertFailsWith<IllegalArgumentException> { DecisionInputItem.Message(image, DecisionRole.Developer) }
        assertFailsWith<IllegalArgumentException> { DecisionInputItem.Message(image, DecisionRole.Assistant) }
        assertFailsWith<IllegalArgumentException> { DecisionInputItem.Message(DecisionContent.Text("hi"), phase = "commentary") }
        val cached = DecisionContent.Parts(listOf(DecisionContentPart.InputText("prefix", DecisionPromptCacheBreakpoint())))
        assertFailsWith<IllegalArgumentException> { DecisionQuestion.Predicate(cached) }
        val json = DecisionContent.Parts(listOf(DecisionContentPart.InputJson(buildJsonObject { put("value", true) })))
        assertFailsWith<IllegalArgumentException> { DecisionInputItem.FunctionCallOutput("call_1", json) }
        assertFailsWith<IllegalArgumentException> { DecisionContentPart.InputJson(JsonPrimitive(1)) }
        assertFailsWith<SerializationException> { Json.decodeFromString<DecisionValue>("1") }
        assertFailsWith<SerializationException> { Json.decodeFromString<DecisionInput>("true") }
        assertFailsWith<IllegalArgumentException> {
            DecisionQuestion.Choice(DecisionContent.Text("Pick one."), listOf(DecisionChoice(DecisionValue.Text("same")), DecisionChoice(DecisionValue.Text("same"))))
        }
    }
}
