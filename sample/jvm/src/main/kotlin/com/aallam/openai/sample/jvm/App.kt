package com.aallam.openai.sample.jvm

import com.aallam.openai.api.ExperimentalOpenAI
import com.aallam.openai.api.answer.AnswerRequest
import com.aallam.openai.api.answer.QuestionAnswer
import com.aallam.openai.api.classification.ClassificationRequest
import com.aallam.openai.api.classification.LabeledExample
import com.aallam.openai.api.completion.CompletionRequest
import com.aallam.openai.api.engine.Ada
import com.aallam.openai.api.engine.Curie
import com.aallam.openai.api.engine.Engine
import com.aallam.openai.api.search.SearchRequest
import com.aallam.openai.client.OpenAI
import kotlinx.coroutines.flow.launchIn
import kotlinx.coroutines.flow.onCompletion
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.runBlocking

@ExperimentalOpenAI
fun main() = runBlocking {
    val apiKey = System.getenv("OPENAI_API_KEY")
    val token = requireNotNull(apiKey) { "OPENAI_API_KEY environment variable must be set." }
    val openAI = OpenAI(token)

    println("> Getting available engines...")
    openAI.engines().forEach(::println)

    println("\n> Getting ada engine...")
    val ada: Engine = openAI.engine(Ada)
    println(ada)

    println("\n>️ Creating completion...")
    val completionRequest = CompletionRequest(prompt = "Somebody once told me the world is gonna roll me")
    openAI.completion(Ada, completionRequest).choices.forEach(::println)

    println("\n>️ Creating completion stream...")
    openAI.completions(Ada, completionRequest)
        .onEach { print(it.choices[0].text) }
        .onCompletion { println() }
        .launchIn(this)
        .join()

    println("\n> Searching documents...")
    val searchRequest = SearchRequest(
        documents = listOf("Water", "Earth", "Electricity", "Fire"),
        query = "Pikachu"
    )
    openAI.search(Ada, searchRequest).forEach(::println)

    println("\n> Classifying...")
    val classificationRequest = ClassificationRequest(
        model = Curie,
        query = "It is a raining day :(",
        searchModel = Ada,
        labels = listOf("Positive", "Negative", "Neutral"),
        examples = listOf(
            LabeledExample("A happy moment", "Positive"),
            LabeledExample("I am sad.", "Negative"),
            LabeledExample("I am feeling awesome", "Positive"),
        )
    )
    val classification = openAI.classifications(classificationRequest)
    println(classification.label)

    println("\n> Answers...")
    val answersRequest = AnswerRequest(
        model = Curie,
        question = "which puppy is happy?",
        searchModel = Ada,
        examples = listOf(
            QuestionAnswer(
                question = "What is human life expectancy in the United States?",
                answer = "78 years."
            )
        ),
        examplesContext = "In 2017, U.S. life expectancy was 78.6 years.",
        maxTokens = 5,
        stop = listOf("\n", "<|endoftext|>"),
        documents = listOf("Puppy A is happy.", "Puppy B is sad.")
    )
    val answer = openAI.answers(answersRequest)
    println(answer.answers)

    println("\n> Read files...")
    val files = openAI.files()
    println(files)
}
