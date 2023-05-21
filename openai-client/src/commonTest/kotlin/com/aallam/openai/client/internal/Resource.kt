package com.aallam.openai.client.internal

import okio.FileSystem
import okio.Path
import okio.Path.Companion.toPath

/**
 * File system to access test files.
 */
internal expect val TestFileSystem: FileSystem

/**
 * Get [Path] of a given [fileName] test file.
 */
fun testFilePath(fileName: String): Path = libRoot / "openai-client/src/commonTest/resources" / fileName

/**
 * Get the library lib root.
 */
private val libRoot
    get() = env("LIB_ROOT")?.toPath() ?: error("Can't find `LIB_ROOT` environment variable")


