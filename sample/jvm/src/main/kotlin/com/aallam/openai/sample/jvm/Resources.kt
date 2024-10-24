package com.aallam.openai.sample.jvm

import kotlinx.io.files.Path

object Resources {
    fun path(resource: String): Path {
        return Path(path = getPath(resource))
    }

    private fun getPath(resource: String): String {
        return Resources::class.java.getResource("/$resource")?.path
            ?: throw IllegalStateException("Resource $resource not found")
    }
}
