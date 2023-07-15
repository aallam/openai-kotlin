package com.aallam.openai.api.core

public data class OpenAIResponse<T>(val headers: Map<String, List<String>>, val body: T)