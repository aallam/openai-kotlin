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
