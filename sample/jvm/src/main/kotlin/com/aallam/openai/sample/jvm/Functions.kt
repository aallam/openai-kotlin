package com.aallam.openai.sample.jvm

import com.aallam.openai.api.chat.ToolCall
import kotlinx.serialization.json.JsonObject
import kotlinx.serialization.json.jsonPrimitive

/**
 * Executes a function call and returns its result.
 */
internal fun ToolCall.Function.execute(): String {
    val functionToCall = availableFunctions[function.name] ?: error("Function ${function.name} not found")
    val functionArgs = function.argumentsAsJson()
    return functionToCall(functionArgs)
}


/**
 * A map that associates function names with their corresponding functions.
 */
private val availableFunctions = mapOf(
    "currentWeather" to ::callCurrentWeather,
    "nickname" to ::callNickname,
)

/**
 * Example of a fake function for retrieving weather information based on location and temperature unit.
 * In a production scenario, this function could be replaced with an actual backend or external API call.
 */
private fun callCurrentWeather(args: JsonObject): String {
    val location = args.getValue("location").jsonPrimitive.content
    return when {
        location.contains("San Francisco", ignoreCase = true) ->
            """{"location": "San Francisco", "temperature": "72", "unit": "fahrenheit"}"""

        location.contains("Tokyo", ignoreCase = true) ->
            """{"location": "Tokyo", "temperature": "10", "unit": "celsius"}"""

        location.contains("Paris", ignoreCase = true) ->
            """{"location": "Paris", "temperature": "22", "unit": "celsius"}"""

        else ->
            """{"location": "$location", "temperature": "unknown", "unit": "unknown"}"""
    }
}


/**
 * Example of a fake function for retrieving a city's nickname based on location.
 */
private fun callNickname(args: JsonObject): String {
    val location = args.getValue("location").jsonPrimitive.content
    return when {
        location.contains("San Francisco", ignoreCase = true) -> "The Golden City"
        location.contains("Tokyo", ignoreCase = true) -> "The Big Mikan"
        location.contains("Paris", ignoreCase = true) -> "The City of Lights"
        else -> "The City of $location"
    }
}
