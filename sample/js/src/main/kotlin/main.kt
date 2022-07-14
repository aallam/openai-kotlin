import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.model.ModelId
import com.aallam.openai.api.moderation.ModerationRequest
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlin.coroutines.coroutineContext

suspend fun main() {
    val apiKey = js("process.env.OPENAI_API_KEY").unsafeCast<String?>()
    val token = requireNotNull(apiKey) { "OPENAI_API_KEY environment variable must be set." }
    val openAI = OpenAI(token = token)

    println("> Getting available models...")
    openAI.models().forEach(::println)

    println("\n> Getting ada model...")
    val ada = openAI.model(modelId = ModelId("text-ada-001"))
    println(ada)

    println("\n>️ Creating completion...")
    val completionRequest = CompletionRequest(
        model = ada.id,
        prompt = "Somebody once told me the world is gonna roll me"
    )
    openAI.completion(completionRequest).choices.forEach(::println)

    println("\n>️ Creating completion stream...")
    val scope = CoroutineScope(coroutineContext)
    openAI.completions(completionRequest)
        .onEach { print(it.choices[0].text) }
        .onCompletion { println() }
        .launchIn(scope)
        .join()

    println("\n> Read files...")
    val files = openAI.files()
    println(files)

    println("\n> Create moderations...")
    val moderation = openAI.moderations(
        request = ModerationRequest(
            input = "I want to kill them."
        )
    )
    println(moderation)
}
