package com.aallam.openai.client.internal

import com.aallam.openai.api.core.Usage
import com.aallam.openai.api.decision.DecisionUsage
import com.aallam.openai.api.image.ImageUsage
import com.aallam.openai.api.model.ModelId
import kotlinx.serialization.json.JsonObjectBuilder
import kotlinx.serialization.json.buildJsonObject
import kotlinx.serialization.json.put

internal fun reportUsage(operation: String, model: ModelId, usage: Usage?) {
    reportLiveUsage(operation, model) {
        put("usage_received", if (usage == null) 0 else 1)
        usage?.promptTokens?.let { put("input_tokens", it) }
        usage?.completionTokens?.let { put("output_tokens", it) }
        usage?.totalTokens?.let { put("total_tokens", it) }
    }
}

internal fun reportImageUsage(operation: String, model: ModelId, usage: ImageUsage?) {
    reportLiveUsage(operation, model) {
        put("usage_received", if (usage == null) 0 else 1)
        usage?.let {
            put("input_tokens", it.inputTokens)
            put("output_tokens", it.outputTokens)
            put("total_tokens", it.totalTokens)
            it.inputTokensDetails?.textTokens?.let { tokens -> put("input_text_tokens", tokens) }
            it.inputTokensDetails?.imageTokens?.let { tokens -> put("input_image_tokens", tokens) }
            it.outputTokensDetails?.textTokens?.let { tokens -> put("output_text_tokens", tokens) }
            it.outputTokensDetails?.imageTokens?.let { tokens -> put("output_image_tokens", tokens) }
        }
    }
}

internal fun reportAudioUsage(operation: String, model: ModelId, seconds: Double) {
    reportLiveUsage(operation, model) { put("audio_seconds", seconds) }
}

internal fun reportSpeechUsage(model: ModelId, characters: Int) {
    reportLiveUsage("speech", model) { put("characters", characters) }
}

internal fun reportDecisionUsage(model: ModelId, usage: DecisionUsage) {
    reportLiveUsage("decisions", model) {
        put("usage_received", 1)
        put("input_tokens", usage.inputTokens)
        put("output_tokens", usage.outputTokens)
        put("total_tokens", usage.totalTokens)
        put("cached_tokens", usage.inputTokensDetails.cachedTokens)
        put("cache_write_tokens", usage.inputTokensDetails.cacheWriteTokens)
        put("reasoning_tokens", usage.outputTokensDetails.reasoningTokens)
    }
}

private fun reportLiveUsage(operation: String, model: ModelId, metrics: JsonObjectBuilder.() -> Unit) {
    val record = buildJsonObject {
        put("operation", operation)
        put("model", model.id)
        put("requests", 1)
        metrics()
    }
    println("OPENAI_LIVE_USAGE $record")
}
