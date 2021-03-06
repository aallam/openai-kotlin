package com.aallam.openai.client

import com.aallam.openai.api.Engine
import com.aallam.openai.api.search.SearchRequest
import kotlinx.coroutines.runBlocking
import org.junit.Test
import kotlin.test.assertEquals

class TestOpenAI {

  private val openAI = OpenAI(config)

  @Test
  fun search() {
    runBlocking {
      val documents = listOf("White House", "hospital", "school")
      val query = "the president"
      val request = SearchRequest(documents, query)

      val response = openAI.search(Engine.Davinci, request)

      assertEquals(documents.size, response.data.size)
    }
  }
}
