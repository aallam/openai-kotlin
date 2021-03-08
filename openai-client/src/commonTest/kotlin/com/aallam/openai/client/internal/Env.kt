package com.aallam.openai.client.internal

/**
 * Get system environment variable by name.
 */
internal expect fun env(name: String): String?
