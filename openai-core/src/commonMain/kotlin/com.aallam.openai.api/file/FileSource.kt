package com.aallam.openai.api.file

import okio.Source

/**
 * Represents a file resource.
 */
public class FileSource(
    /**
     * File name.
     */
    public val name: String,

    /**
     * File source.
     */
    public val source: Source,
)

/**
 * Represents a file resource.
 */
public fun fileSource(block: FileSourceBuilder.() -> Unit): FileSource = FileSourceBuilder().apply(block).build()

/**
 * Builder of [FileSource] instances.
 */
public class FileSourceBuilder {

    /**
     * File name.
     */
    public var name: String? = null

    /**
     * File source.
     */
    public var source: Source? = null

    /**
     * Creates the [FileSource] instance
     */
    public fun build(): FileSource = FileSource(
        name = requireNotNull(name) { "name is required" },
        source = requireNotNull(source) { "source is required" }
    )
}
