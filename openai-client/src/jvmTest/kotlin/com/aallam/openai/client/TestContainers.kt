package com.aallam.openai.client

import com.aallam.openai.api.container.*
import com.aallam.openai.api.file.FileSource
import com.aallam.openai.api.logging.LogLevel
import com.aallam.openai.client.internal.withTestCleanup
import kotlinx.io.Buffer
import kotlin.test.*

class TestContainers : TestOpenAI() {
    @Test
    fun containerAndFileLifecycle() = test {
        val client = OpenAI(OpenAIConfig(token = token, logging = LoggingConfig(LogLevel.None), retry = RetryStrategy(maxRetries = 0)))
        try {
            withTestCleanup {
                val container = client.createContainer(ContainerRequest(
                    name = "openai-kotlin-live-test", memoryLimit = ContainerMemoryLimit.G1,
                    expiresAfter = ContainerExpirationPolicy(1), networkPolicy = ContainerNetworkPolicy.Disabled,
                ))
                var containerDeleted = false
                afterTest { if (!containerDeleted) client.deleteContainer(container.id) }
                assertEquals(container.id, client.retrieveContainer(container.id)?.id)
                assertTrue(client.containersPage(ContainerListQuery(name = container.name, limit = 100)).any { it.id == container.id })

                val bytes = "container fixture\n".encodeToByteArray()
                val file = client.uploadContainerFile(container.id, FileSource("fixture.txt", Buffer().apply { write(bytes) }))
                var fileDeleted = false
                afterTest { if (!fileDeleted) client.deleteContainerFile(container.id, file.id) }
                assertEquals(container.id, file.containerId)
                assertEquals(bytes.size.toLong(), file.bytes)
                assertEquals(file.id, client.retrieveContainerFile(container.id, file.id)?.id)
                assertTrue(client.containerFilesPage(container.id).any { it.id == file.id })
                assertContentEquals(bytes, client.downloadContainerFile(container.id, file.id))

                assertTrue(client.deleteContainerFile(container.id, file.id))
                fileDeleted = true
                assertTrue(client.deleteContainer(container.id))
                containerDeleted = true
            }
        } finally {
            client.close()
        }
    }
}
