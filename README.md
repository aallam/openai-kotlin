# OpenAI API Kotlin Client

[![Maven Central](https://img.shields.io/maven-central/v/com.aallam.openai/openai-client?color=blue&label=Download)](https://search.maven.org/artifact/com.aallam.openai/openai-client)
[![License](https://img.shields.io/github/license/Aallam/openai-kotlin?color=yellow)](LICENSE.md)
[![Kotlin](https://img.shields.io/badge/kotlin-1.5.10-blue.svg?logo=kotlin)](https://kotlinlang.org/docs/releases.html#release-details)

A simple Kotlin client for [OpenAI's API](https://beta.openai.com/docs/api-reference) with multiplatform and coroutines capabilities. 

## 🛠 Setup

Install OpenAI API Kotlin client by adding the following dependency to your `gradle.build` file:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.aallam.openai:openai-client:$kotlin_client_version"
}
```
Choose and add to your dependencies one of [Ktor's engines](https://ktor.io/docs/http-client-engines.html).

### Multiplaform
In multiplatform projects, add openai client dependency to `common`, and choose an [engine](https://ktor.io/docs/http-client-engines.html) for each target.

## 💡 Getting Started

Create an instance of `OpenAI` client:
```kotlin
val openAI = OpenAI(apiKey)
```
Use your `OpenAI` instance to make API requests:
* [List engines](https://beta.openai.com/docs/api-reference/engines/list)
```kotlin
val engines: List<Engine> = openAI.engines()
```
* [Retrieve an engine](https://beta.openai.com/docs/api-reference/engines/retrieve)
```kotlin
val ada: Engine = openAI.engine(Ada)
```
* [Create completion](https://beta.openai.com/docs/api-reference/completions/create)
```kotlin
val completionRequest = CompletionRequest(
    prompt = "Somebody once told me the world is gonna roll me",
    echo = true
)
val completion: TextCompletion = openAI.completion(Ada, completionRequest)
```
* [Create completion stream](https://beta.openai.com/docs/api-reference/completions/create-via-get)
```kotlin
val completions: Flow<TextCompletion> = openAI.completions(Ada, completionRequest)
```
* [Create search](https://beta.openai.com/docs/api-reference/searches/create)
```kotlin
val searchRequest = SearchRequest(
    documents = listOf("Water", "Earth", "Electricity", "Fire"),
    query = "Pikachu"
)
val search: List<SearchResult> = openAI.search(Ada, searchRequest)
```
* [Create classification](https://beta.openai.com/docs/api-reference/classifications/create)
```kotlin
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
```
* [Create answer](https://beta.openai.com/docs/api-reference/answers/create)
```kotlin
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
```
* [List files](https://beta.openai.com/docs/api-reference/files/list)
````kotlin
val files = openAI.files()
````

## ℹ️ Sample apps

Sample apps are available under `sample`, please check the [README](sample/readme.md) for running instructions.

## 📄 License

OpenAI API Kotlin Client is an open-sourced software licensed under the [MIT license](LICENSE.md).
