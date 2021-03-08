import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.engine.Engine
import com.aallam.openai.api.engine.EngineId
import com.aallam.openai.api.search.SearchRequest
import com.aallam.openai.client.OpenAI

suspend fun main() {
    val apiKey = js("process.env.OPENAI_API_KEY").unsafeCast<String?>()
    val token = requireNotNull(apiKey) { "OPENAI_API_KEY environment variable must be set." }
    val openAI = OpenAI(token = token)

    println("> Getting available engines...")
    openAI.engines().forEach(::println)

    println("\n> Getting ada engine...")
    val ada: Engine = openAI.engine(EngineId.Ada)
    println(ada)

    println("\n>️ Creating completion...")
    val completionRequest = CompletionRequest(
        prompt = "Somebody once told me the world is gonna roll me",
        echo = true
    )
    openAI.createCompletion(EngineId.Ada, completionRequest).choices.forEach(::println)

    println("\n> Searching documents...")
    val searchRequest = SearchRequest(
        documents = listOf("Water", "Earth", "Electricity", "Fire"),
        query = "Pikachu"
    )
    openAI.search(EngineId.Ada, searchRequest).forEach(::println)
}
