package com.aallam.openai.api.misc

import com.aallam.openai.api.finetuning.Hyperparameters
import kotlinx.serialization.json.Json
import kotlin.test.Test
import kotlin.test.assertEquals

class TestHyperparameters {

    @Test
    fun serializeHyperparametersNEpochsAuto() {
        val params = Hyperparameters(nEpochs = Hyperparameters.NEpochs.Auto)
        val encodedParams = Json.encodeToString(Hyperparameters.serializer(), params)
        val json = """{"n_epochs":"auto"}"""
        assertEquals(encodedParams, json)
        val decodedAuto = Json.decodeFromString(Hyperparameters.serializer(), json)
        assertEquals(params, decodedAuto)
    }

    @Test
    fun serializeHyperparametersNEpochsInt() {
        val params = Hyperparameters(nEpochs = Hyperparameters.NEpochs(50))
        val encodedParams = Json.encodeToString(Hyperparameters.serializer(), params)
        val json = """{"n_epochs":50}"""
        assertEquals(encodedParams, json)
        assertEquals(encodedParams, json)
        val decodedAuto = Json.decodeFromString(Hyperparameters.serializer(), json)
        assertEquals(params, decodedAuto)
    }

    @Test
    fun serializeHyperparametersNEpochsString() {
        val params = Hyperparameters(nEpochs = Hyperparameters.NEpochs("auto"))
        val encodedParams = Json.encodeToString(Hyperparameters.serializer(), params)
        val json = """{"n_epochs":"auto"}"""
        assertEquals(encodedParams, json)
        assertEquals(encodedParams, json)
        val decodedAuto = Json.decodeFromString(Hyperparameters.serializer(), json)
        assertEquals(params, decodedAuto)
    }
}
