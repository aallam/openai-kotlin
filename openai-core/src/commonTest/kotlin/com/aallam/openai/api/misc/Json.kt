package com.aallam.openai.api.misc

import kotlinx.serialization.json.Json

val JsonLenient = Json {
    isLenient = true
    ignoreUnknownKeys = true
}
