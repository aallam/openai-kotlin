package com.aallam.openai.client

/**
 * Defines a closeable resource.
 * This will be replaced by [AutoCloseable] once it becomes stable.
 */
public expect interface Closeable {

    /**
     * Closes underlying resources
     */
    public fun close()
}