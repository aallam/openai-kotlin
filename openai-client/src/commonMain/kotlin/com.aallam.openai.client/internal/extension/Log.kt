package com.aallam.openai.client.internal.extension

import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.api.logging.Logger
import io.ktor.client.plugins.logging.DEFAULT
import io.ktor.client.plugins.logging.EMPTY
import io.ktor.client.plugins.logging.SIMPLE
import io.ktor.client.plugins.logging.LogLevel as KLogLevel
import io.ktor.client.plugins.logging.Logger as KLogger

/**
 * Convert Logger to a Ktor's Logger.
 */
internal fun Logger.toKtorLogger() = when (this) {
    Logger.Default -> KLogger.DEFAULT
    Logger.Simple -> KLogger.SIMPLE
    Logger.Empty -> KLogger.EMPTY
}

/**
 * Convert LogLevel to a Ktor's LogLevel.
 */
internal fun LogLevel.toKtorLogLevel() = when (this) {
    LogLevel.All -> KLogLevel.ALL
    LogLevel.Headers -> KLogLevel.HEADERS
    LogLevel.Body -> KLogLevel.BODY
    LogLevel.Info -> KLogLevel.INFO
    LogLevel.None -> KLogLevel.NONE
}
