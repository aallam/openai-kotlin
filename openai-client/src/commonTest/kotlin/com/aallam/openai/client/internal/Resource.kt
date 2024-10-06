package com.aallam.openai.client.internal

import kotlinx.io.files.Path
import kotlinx.io.files.SystemFileSystem

/**
 * File system to access test files.
 */
internal val TestFileSystem = SystemFileSystem

/**
 * Get [Path] of a given [fileName] test file.
 */
fun testFilePath(fileName: String): Path = Path(libRoot, "openai-client/src/commonTest/resources", fileName)

/**
 * Get the library lib root.
 */
private val libRoot: String
    get() = env("LIB_ROOT") ?: error("Can't find `LIB_ROOT` environment variable")
