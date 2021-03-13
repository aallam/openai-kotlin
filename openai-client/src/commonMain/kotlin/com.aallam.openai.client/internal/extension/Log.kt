package com.aallam.openai.client.internal.extension

import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import io.ktor.client.features.logging.*
import io.ktor.client.features.logging.LogLevel as KLogLevel
import io.ktor.client.features.logging.Logger as KLogger

/**
 * Convert Logger to a Ktor's Logger.
 */
internal fun Logger.toKLogger() = when (this) {
    Logger.Default -> KLogger.DEFAULT
    Logger.Simple -> KLogger.SIMPLE
    Logger.Empty -> KLogger.EMPTY
}

/**
 * Convert LogLevel to a Ktor's LogLevel.
 */
internal fun LogLevel.toKLogLevel() = when (this) {
    LogLevel.All -> KLogLevel.ALL
    LogLevel.Headers -> KLogLevel.HEADERS
    LogLevel.Body -> KLogLevel.BODY
    LogLevel.Info -> KLogLevel.INFO
    LogLevel.None -> KLogLevel.NONE
}
