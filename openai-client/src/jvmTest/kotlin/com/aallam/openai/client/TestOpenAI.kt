package com.aallam.openai.client

import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.api.search.SearchRequest
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotEquals

class TestOpenAI {

  private val openAI = OpenAI(config)

  @Test
  fun search() {
    runBlocking {
      val documents = listOf("White House", "hospital", "school")
      val query = "the president"
      val request = SearchRequest(documents, query)
      val response = openAI.search(EngineId.Davinci, request)
      assertEquals(documents.size, response.data.size)
    }
  }

  @Test
  fun engines() {
    runBlocking {
      val response = openAI.engines()
      assertNotEquals(0, response.data.size)
    }
  }

  @Test
  fun engine() {
    runBlocking {
      val engineId = EngineId.Davinci
      val response = openAI.engine(engineId)
      assertEquals(engineId, response.id)
    }
  }
}
