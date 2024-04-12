package com.aallam.openai.api.core

/**
 * Represents a stream event.
 */
public data class Event<T>(
    /**
     * The event type.
     */
    val event: EventType,
    /**
     * The event data.
     */
    val data: T
)
