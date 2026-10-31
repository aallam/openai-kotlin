package com.aallam.openai.api.finetuning

import kotlinx.serialization.SerializationException
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.*
import kotlin.test.*

class TestFineTuningOperations {
    @Test
    fun numericAndAutomaticHyperparametersRetainCurrentShapes() {
        val value = Json.decodeFromString<FineTuningHyperparameters>("""{
            "batch_size":"auto","learning_rate_multiplier":0.125,"n_epochs":2
        }""")
        assertEquals(FineTuningNumber.Automatic("auto"), value.batchSize)
        assertEquals(FineTuningNumber.Number(0.125), value.learningRateMultiplier)
        assertEquals(FineTuningNumber.Number(2.0), value.nEpochs)
        val encoded = Json.encodeToJsonElement(value).jsonObject
        assertEquals(JsonPrimitive("auto"), encoded["batch_size"])
        assertEquals(0.125, encoded.getValue("learning_rate_multiplier").jsonPrimitive.double)
        assertEquals(value, Json.decodeFromJsonElement<FineTuningHyperparameters>(encoded))
        assertEquals(FineTuningHyperparameters(), Json.decodeFromString<FineTuningHyperparameters>("{}"))
        assertFailsWith<SerializationException> { Json.decodeFromString<FineTuningNumber>("\"other\"") }
        assertFailsWith<SerializationException> { Json.decodeFromString<FineTuningNumber>("true") }
    }

    @Test
    fun currentJobDetailsKeepIntegrationMetadataAndNullableFields() {
        val value = Json.decodeFromString<FineTuningJobDetails>("""{
            "id":"ftjob-1","created_at":3000000000,"model":"model","object":"fine_tuning.job",
            "organization_id":"org-1","status":"paused","training_file":"file-1","result_files":["file-result"],
            "error":null,"fine_tuned_model":null,"finished_at":null,"seed":42,"trained_tokens":3000000000,
            "validation_file":null,"estimated_finish":3000000010,"hyperparameters":null,
            "integrations":[{"type":"wandb","wandb":{"project":"training","entity":"team","name":"run","tags":["tag"]}}],
            "metadata":{"experiment":"trial"},"method":{"type":"supervised","supervised":{"hyperparameters":{"batch_size":"auto"}}}
        }""")
        assertEquals("paused", value.status.value)
        assertEquals(3000000000L, value.createdAt)
        assertEquals(3000000000L, value.trainedTokens)
        assertEquals(42L, value.seed)
        assertNull(value.hyperparameters)
        assertEquals(FineTuningWandbIntegration("wandb", FineTuningWandbSettings("training", "team", "run", listOf("tag"))), value.integrations?.single())
        assertEquals(mapOf("experiment" to "trial"), value.metadata)
        assertEquals("auto", value.method?.get("supervised")?.jsonObject?.get("hyperparameters")?.jsonObject?.get("batch_size")?.jsonPrimitive?.content)
        assertEquals(value, Json.decodeFromString<FineTuningJobDetails>(Json.encodeToString(value)))
    }
}
