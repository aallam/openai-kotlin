package com.aallam.openai.client.internal

import okio.FileSystem
import okio.NodeJsFileSystem

internal actual val ClientFileSystem: FileSystem = NodeJsFileSystem
