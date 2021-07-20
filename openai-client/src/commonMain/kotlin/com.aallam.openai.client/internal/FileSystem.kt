package com.aallam.openai.client.internal

import okio.FileSystem

/**
 * Client's default file system.
 */
internal expect val ClientFileSystem: FileSystem
