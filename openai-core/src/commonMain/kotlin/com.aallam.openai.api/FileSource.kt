package com.aallam.openai.api

import okio.Source

@ExperimentalOpenAI
public class FileSource(
    public val filename: String,
    public val source: Source,
)
