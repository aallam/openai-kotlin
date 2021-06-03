package com.aallam.openai.client.internal

import okio.ExperimentalFileSystem
import okio.FileSystem

internal actual val ClientFileSystem: FileSystem = FileSystem.SYSTEM
