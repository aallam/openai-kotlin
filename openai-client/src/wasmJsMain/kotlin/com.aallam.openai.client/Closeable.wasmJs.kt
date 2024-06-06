package com.aallam.openai.client

/**
 * Defines a closeable resource.
 * This will be replaced by [AutoCloseable] once it becomes stable.
 */
public actual interface Closeable {
    /**
     * Closes underlying resources
     */
    public actual fun close()

}