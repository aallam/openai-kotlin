package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.decision.*
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.http.content.TextContent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import kotlin.test.*

class TestOfflineDecisions {
    @Test
    fun decisionSendsTypedQuestionsAndPreservesAnswersAndUsage() = runTest {
        val engine = MockEngine { request ->
            assertEquals(HttpMethod.Post, request.method)
            assertEquals("/v1/decisions", request.url.encodedPath)
            assertEquals("value", request.headers["X-Test"])
            assertEquals("query-value", request.url.parameters["custom"])
            assertEquals(ContentType.Application.Json, request.body.contentType?.withoutParameters())
            val body = Json.parseToJsonElement((request.body as TextContent).text)
            assertEquals(Json.parseToJsonElement("""{
                "model":"gpt-6-luna","input":"A red parcel.","questions":[
                    {"type":"predicate","instructions":"Is the parcel red?","name":"red"},
                    {"type":"choice","instructions":"Select its color.","choices":[{"value":true},{"value":"true","description":"The word true"}],"name":"color"},
                    {"type":"score","instructions":"Rate urgency.","levels":[{"label":"low"},{"label":"high"}],"name":"urgency"},
                    {"type":"predicate","instructions":"An unsupported question.","name":"declined"}
                ]} """), body)
            respond(decisionJson, headers = jsonHeaders)
        }
        client(engine).use { client ->
            val result = client.decision(request(), RequestOptions(
                headers = mapOf("X-Test" to "value"), urlParameters = mapOf("custom" to "query-value"),
            ))
            assertEquals(listOf("red", "color", "urgency", "declined"), result.answers.map { it.name })
            assertEquals(0.99, assertIs<DecisionAnswer.Predicate>(result.answers[0]).probability)
            val choice = assertIs<DecisionAnswer.Choice>(result.answers[1])
            assertEquals(DecisionValue.Boolean(true), choice.choice)
            assertEquals(listOf(DecisionValue.Boolean(true), DecisionValue.Text("true")), choice.probabilities.map { it.value })
            val score = assertIs<DecisionAnswer.Score>(result.answers[2])
            assertEquals(0.75, score.score)
            assertEquals(listOf(0L, 1L), score.probabilities.map { it.value })
            assertIs<DecisionAnswer.Refusal>(result.answers[3])
            assertEquals(ModelId("gpt-6-luna"), result.model)
            assertEquals(13, result.usage.totalTokens)
            assertEquals(2, result.usage.inputTokensDetails.cacheWriteTokens)
            assertEquals(3, result.usage.inputTokensDetails.cachedTokens)
            assertEquals(0, result.usage.outputTokensDetails.reasoningTokens)
        }
    }

    @Test
    fun conversationPartsAndCacheControlsReachTheWire() = runTest {
        val engine = MockEngine { request ->
            val body = Json.parseToJsonElement((request.body as TextContent).text).jsonObject
            assertEquals(Json.parseToJsonElement("""[
                {"type":"message","role":"user","content":[
                    {"type":"input_text","text":"Inspect this parcel.","prompt_cache_breakpoint":{"mode":"explicit"}},
                    {"type":"input_image","image_url":"https://example.com/parcel.jpg","detail":"low"},
                    {"type":"input_json","data":{"urgent":true}}
                ]},
                {"type":"function_call","name":"lookup","call_id":"call_1","arguments":"{}"},
                {"type":"function_call_output","call_id":"call_1","output":"red"}
            ]"""), body["input"])
            assertEquals(JsonPrimitive("parcel-prefix"), body["prompt_cache_key"])
            assertEquals(Json.parseToJsonElement("""{"mode":"explicit","ttl":"30m"}"""), body["prompt_cache_options"])
            assertEquals(JsonPrimitive("user-1"), body["safety_identifier"])
            respond(decisionJson, headers = jsonHeaders)
        }
        val request = request().copy(
            input = DecisionInput.Items(listOf(
                DecisionInputItem.Message(DecisionContent.Parts(listOf(
                    DecisionContentPart.InputText("Inspect this parcel.", DecisionPromptCacheBreakpoint()),
                    DecisionContentPart.InputImage("https://example.com/parcel.jpg", "low"),
                    DecisionContentPart.InputJson(buildJsonObject { put("urgent", true) }),
                ))),
                DecisionInputItem.FunctionCall(arguments = "{}", callId = "call_1", name = "lookup"),
                DecisionInputItem.FunctionCallOutput(callId = "call_1", output = DecisionContent.Text("red")),
            )),
            promptCacheKey = "parcel-prefix",
            promptCacheOptions = DecisionPromptCacheOptions(mode = "explicit", ttl = "30m"),
            safetyIdentifier = "user-1",
        )
        client(engine).use { client -> assertEquals(4, client.decision(request).answers.size) }
    }

    @Test
    fun invalidRequestsKeepApiErrorDetails() = runTest {
        client(MockEngine {
            respond("""{"error":{"message":"Invalid question","type":"invalid_request_error","param":"questions","code":"invalid_value"}}""",
                HttpStatusCode.BadRequest, jsonHeaders)
        }).use { client ->
            val error = assertFailsWith<InvalidRequestException> { client.decision(request()) }
            assertEquals("questions", error.error.detail?.param)
            assertEquals("invalid_value", error.error.detail?.code)
        }
    }

    @Test
    fun customClientsCanOmitDecisionSupport() = runTest {
        val custom = object : Decisions {}
        assertFailsWith<UnsupportedOperationException> { custom.decision(request()) }
    }

    private fun request() = decisionRequest {
        model = ModelId("gpt-6-luna")
        input = DecisionInput.Text("A red parcel.")
        questions = listOf(
            DecisionQuestion.Predicate(DecisionContent.Text("Is the parcel red?"), "red"),
            DecisionQuestion.Choice(DecisionContent.Text("Select its color."), listOf(
                DecisionChoice(DecisionValue.Boolean(true)), DecisionChoice(DecisionValue.Text("true"), "The word true"),
            ), "color"),
            DecisionQuestion.Score(DecisionContent.Text("Rate urgency."), listOf(DecisionScoreLevel("low"), DecisionScoreLevel("high")), "urgency"),
            DecisionQuestion.Predicate(DecisionContent.Text("An unsupported question."), "declined"),
        )
    }

    private fun client(engine: MockEngine): OpenAI = OpenAI(OpenAIConfig(
        token = "test", engine = engine, retry = RetryStrategy(maxRetries = 0), logging = LoggingConfig(LogLevel.None),
    ))

    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val decisionJson = """{
        "model":"gpt-6-luna","answers":[
            {"type":"predicate","name":"red","probability":0.99},
            {"type":"choice","name":"color","choice":true,"confidence":0.9,"probabilities":[{"value":true,"probability":0.9},{"value":"true","probability":0.1}]},
            {"type":"score","name":"urgency","score":0.75,"confidence":0.75,"probabilities":[{"value":0,"label":"low","probability":0.25},{"value":1,"label":"high","probability":0.75}]},
            {"type":"refusal","name":"declined"}
        ],"usage":{"input_tokens":9,"input_tokens_details":{"cache_write_tokens":2,"cached_tokens":3},"output_tokens":4,"output_tokens_details":{"reasoning_tokens":0},"total_tokens":13}
    }"""
}
