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
```kotlin
// Get available OpenAI engines
val engines: List<Engine> = openAI.engines()

// Get an engine details
val ada: Engine = openAI.engine(EngineId.Ada)

// Create a completion
val completionRequest = CompletionRequest(
    prompt = "Somebody once told me the world is gonna roll me",
    echo = true
)
val completion: TextCompletion = openAI.completion(EngineId.Ada, completionRequest)

// Create a completion as a stream
val completions: Flow<TextCompletion> = openAI.completions(EngineId.Ada, completionRequest)

// Search documents
val searchRequest = SearchRequest(
    documents = listOf("Water", "Earth", "Electricity", "Fire"),
    query = "Pikachu"
)
val search: List<SearchResult> = openAI.search(EngineId.Ada, searchRequest)
```

## ℹ️ Sample apps

Sample apps are available under `sample`, please check the [README](sample/readme.md) for running instructions.

## 📄 License

OpenAI API Kotlin Client is an open-sourced software licensed under the [MIT license](LICENSE.md).
