package com.aallam.openai.api.core

import kotlinx.serialization.json.JsonElement

/**
 * Represents a stream event.
 */
public interface Event

/**
 * Represents a custom event.
 */
public class CustomEvent(public val json: JsonElement) : Event
