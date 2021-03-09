# OpenAI API Kotlin Client

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
val engines = openAI.engines()

// Get an engine details
val ada = openAI.engine(EngineId.Ada)

// Create a completion
val completionRequest = CompletionRequest(
    prompt = "Somebody once told me the world is gonna roll me",
    echo = true
)
val completion = openAI.createCompletion(EngineId.Ada, completionRequest)

// Search documents
val searchRequest = SearchRequest(
    documents = listOf("Water", "Earth", "Electricity", "Fire"),
    query = "Pikachu"
)
val search = openAI.search(EngineId.Ada, searchRequest)
```

## ℹ️ Sample apps

Sample apps are available under `sample`, please check the [README](sample/readme.md) for running instructions.

## 📄 License

OpenAI API Kotlin Client is an open-sourced software licensed under the [MIT license](LICENSE.md).
