package com.aallam.openai.client.internal

import okio.FileSystem
import okio.NodeJsFileSystem
import okio.Path.Companion.toPath
import okio.Source

internal actual val TestFileSystem: FileSystem = NodeJsFileSystem
