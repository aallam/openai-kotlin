package com.aallam.openai.client

import com.aallam.openai.api.core.RequestOptions
import com.aallam.openai.api.core.Status
import com.aallam.openai.api.exception.InvalidRequestException
import com.aallam.openai.api.exception.PermissionException
import com.aallam.openai.api.finetuning.*
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.client.internal.api.FineTuningApi
import com.aallam.openai.client.internal.api.FineTuningPermissionsApi
import com.aallam.openai.client.internal.createHttpClient
import com.aallam.openai.client.internal.http.HttpTransport
import io.ktor.client.engine.mock.*
import io.ktor.http.*
import io.ktor.http.content.TextContent
import kotlinx.coroutines.test.runTest
import kotlinx.serialization.json.*
import kotlin.test.*

class TestOfflineFineTuningOperations {
    @Test
    fun pauseResumeAndRetrievalSupportAbsentOrNullHyperparameters() = runTest {
        var requests = 0
        val transport = transport(MockEngine { request ->
            val paths = listOf("/pause", "/resume", "")
            assertEquals("/v1/fine_tuning/jobs/ftjob-test${paths[requests]}", request.url.encodedPath)
            assertEquals(if (requests == 2) HttpMethod.Get else HttpMethod.Post, request.method)
            assertEquals("fixture", request.headers["X-Test"])
            val status = listOf("pausing", "running", "paused")[requests++]
            respond(jobJson(status, if (status == "running") "null" else null), headers = jsonHeaders)
        })
        val api = FineTuningApi(transport)
        try {
            val paused = api.pauseFineTuningJob(FineTuningId("ftjob-test"), options)
            assertEquals(Status("pausing"), paused.status)
            assertNull(paused.hyperparameters)
            assertEquals("reinforcement", paused.method?.get("type")?.jsonPrimitive?.content)
            assertEquals("sample.output_text", paused.method?.get("reinforcement")?.jsonObject?.get("grader")?.jsonObject?.get("input")?.jsonPrimitive?.content)
            val resumed = api.resumeFineTuningJob(FineTuningId("ftjob-test"), options)
            assertEquals(Status.Running, resumed.status)
            assertNull(resumed.hyperparameters)
            assertEquals(Status("paused"), api.fineTuningJobDetails(FineTuningId("ftjob-test"), options)?.status)
            assertEquals(3, requests)
        } finally { transport.close() }
    }

    @Test
    fun checkpointPagesKeepMetricsAndCursors() = runTest {
        val transport = transport(MockEngine { request ->
            assertEquals(HttpMethod.Get, request.method)
            assertEquals("/v1/fine_tuning/jobs/ftjob-test/checkpoints", request.url.encodedPath)
            assertEquals("checkpoint-0", request.url.parameters["after"])
            assertEquals("2", request.url.parameters["limit"])
            assertEquals("fixture", request.headers["X-Test"])
            respond(checkpointJson, headers = jsonHeaders)
        })
        try {
            val page = FineTuningApi(transport).fineTuningJobCheckpoints(FineTuningId("ftjob-test"), "checkpoint-0", 2, options)
            assertTrue(page.hasMore)
            assertEquals("list", page.objectType)
            assertEquals("checkpoint-1", page.firstId)
            assertEquals("checkpoint-1", page.lastId)
            val checkpoint = page.single()
            assertEquals(ModelId(checkpointName), checkpoint.fineTunedModelCheckpoint)
            assertEquals(3000000000L, checkpoint.createdAt)
            assertEquals(10L, checkpoint.stepNumber)
            assertEquals(0.25, checkpoint.metrics.fullValidLoss)
            assertEquals(0.75, checkpoint.metrics.fullValidMeanTokenAccuracy)
            assertEquals(0.4, checkpoint.metrics.validLoss)
            assertEquals(0.8, checkpoint.metrics.validMeanTokenAccuracy)
            assertEquals(0.3, checkpoint.metrics.trainLoss)
            assertEquals(0.9, checkpoint.metrics.trainMeanTokenAccuracy)
            assertEquals(10.0, checkpoint.metrics.step)
        } finally { transport.close() }
    }

    @Test
    fun adminPermissionLifecycleUsesModelIdAndFullWordOrdering() = runTest {
        var requests = 0
        val transport = transport(MockEngine { request ->
            assertEquals("fixture", request.headers["X-Test"])
            val prefix = "/v1/fine_tuning/checkpoints/$checkpointName/permissions"
            when (requests++) {
                0 -> {
                    assertEquals(HttpMethod.Post, request.method)
                    assertEquals(prefix, request.url.encodedPath)
                    assertEquals(Json.parseToJsonElement("""{"project_ids":["proj-1","proj-2"]}"""),
                        Json.parseToJsonElement((request.body as TextContent).text))
                }
                1 -> {
                    assertEquals(HttpMethod.Get, request.method)
                    assertEquals(prefix, request.url.encodedPath)
                    assertEquals("permission-0", request.url.parameters["after"])
                    assertEquals("2", request.url.parameters["limit"])
                    assertEquals("ascending", request.url.parameters["order"])
                    assertEquals("proj-1", request.url.parameters["project_id"])
                }
                2 -> {
                    assertEquals(HttpMethod.Delete, request.method)
                    assertEquals("$prefix/permission-1", request.url.encodedPath)
                }
            }
            respond(if (request.method == HttpMethod.Delete) """{"id":"permission-1","deleted":true,"object":"checkpoint.permission"}"""
                else permissionJson, headers = jsonHeaders)
        })
        val api = FineTuningPermissionsApi(transport)
        try {
            val checkpoint = ModelId(checkpointName)
            val created = api.createFineTuningCheckpointPermissions(checkpoint, FineTuningCheckpointPermissionRequest(listOf("proj-1", "proj-2")), options)
            assertEquals("permission-1", created.single().id)
            assertEquals("proj-1", created.single().projectId)
            assertEquals(3000000000L, created.single().createdAt)
            assertEquals("permission-1", created.lastId)
            assertFalse(created.hasMore)
            val page = api.fineTuningCheckpointPermissions(checkpoint, "permission-0", 2, FineTuningPermissionOrder.Ascending, "proj-1", options)
            assertEquals(created, page)
            val deleted = api.deleteFineTuningCheckpointPermission(checkpoint, "permission-1", options)
            assertTrue(deleted.deleted)
            assertEquals("permission-1", deleted.id)
            assertEquals("checkpoint.permission", deleted.objectType)
            assertEquals(3, requests)
        } finally { transport.close() }
    }

    @Test
    fun errorsAreNotHiddenAndInvalidPathsNeverReachHttp() = runTest {
        var requests = 0
        var status = HttpStatusCode.NotFound
        val transport = transport(MockEngine {
            requests++
            respond("""{"error":{"message":"unavailable","type":"invalid_request_error"}}""", status, jsonHeaders)
        })
        try {
            val jobs = FineTuningApi(transport)
            val permissions = FineTuningPermissionsApi(transport)
            assertNull(jobs.fineTuningJobDetails(FineTuningId("ftjob-missing")))
            assertFailsWith<InvalidRequestException> { jobs.pauseFineTuningJob(FineTuningId("ftjob-missing")) }
            status = HttpStatusCode.Forbidden
            assertFailsWith<PermissionException> { permissions.fineTuningCheckpointPermissions(ModelId(checkpointName)) }
            val before = requests
            for (invalid in listOf("", ".", "..", "id/other", "id?project=x", "id%2Fother", "id#fragment")) {
                assertFails { jobs.fineTuningJobCheckpoints(FineTuningId(invalid)) }
                assertFails { permissions.deleteFineTuningCheckpointPermission(ModelId(invalid), "permission-1") }
                assertFails { permissions.deleteFineTuningCheckpointPermission(ModelId(checkpointName), invalid) }
            }
            assertEquals(before, requests)
        } finally { transport.close() }
    }

    @Test
    fun legacyImplementationsNeedNoNewMethods() = runTest {
        val legacy = object : FineTuning {
            override suspend fun fineTuningJob(request: FineTuningRequest, requestOptions: RequestOptions?): FineTuningJob = error("unused")
            override suspend fun fineTuningJobs(after: String?, limit: Int?, requestOptions: RequestOptions?): List<FineTuningJob> = emptyList()
            override suspend fun fineTuningJob(id: FineTuningId, requestOptions: RequestOptions?): FineTuningJob? = null
            override suspend fun cancel(id: FineTuningId, requestOptions: RequestOptions?): FineTuningJob? = null
            override suspend fun fineTuningEvents(id: FineTuningId, after: String?, limit: Int?, requestOptions: RequestOptions?) = com.aallam.openai.api.core.PaginatedList<FineTuningJobEvent>(emptyList())
        }
        assertFailsWith<UnsupportedOperationException> { legacy.pauseFineTuningJob(FineTuningId("ftjob-1")) }
        val admin = object : FineTuningPermissions {}
        assertFailsWith<UnsupportedOperationException> { admin.fineTuningCheckpointPermissions(ModelId(checkpointName)) }
    }

    private fun transport(engine: MockEngine) = HttpTransport(createHttpClient(OpenAIConfig(
        token = "test", engine = engine, logging = LoggingConfig(LogLevel.None), retry = RetryStrategy(maxRetries = 0),
    )))

    private val options = RequestOptions(headers = mapOf("X-Test" to "fixture"))
    private val jsonHeaders = headersOf(HttpHeaders.ContentType, "application/json")
    private val checkpointName = "ft:model:org:custom:checkpoint-10"
    private fun jobJson(status: String, hyperparameters: String?) = """{
        "id":"ftjob-test","created_at":3000000000,"model":"model","object":"fine_tuning.job","organization_id":"org-test",
        "status":"$status","training_file":"file-training","result_files":[],"error":{},
        "method":{"type":"reinforcement","reinforcement":{"grader":{"type":"string_check","input":"sample.output_text"}}}
        ${hyperparameters?.let { ",\"hyperparameters\":$it" } ?: ""}
    }"""
    private val checkpointJson = """{"data":[{
        "id":"checkpoint-1","created_at":3000000000,"fine_tuned_model_checkpoint":"$checkpointName","fine_tuning_job_id":"ftjob-test",
        "metrics":{"full_valid_loss":0.25,"full_valid_mean_token_accuracy":0.75,"step":10,"train_loss":0.3,"train_mean_token_accuracy":0.9,"valid_loss":0.4,"valid_mean_token_accuracy":0.8},
        "object":"fine_tuning.job.checkpoint","step_number":10
    }],"has_more":true,"object":"list","first_id":"checkpoint-1","last_id":"checkpoint-1"}"""
    private val permissionJson = """{"data":[{"id":"permission-1","created_at":3000000000,"object":"checkpoint.permission","project_id":"proj-1"}],"has_more":false,"object":"list","first_id":"permission-1","last_id":"permission-1"}"""
}
