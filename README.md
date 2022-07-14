# OpenAI Kotlin API Client

[![Maven Central](https://img.shields.io/maven-central/v/com.aallam.openai/openai-client?color=blue&label=Download)](https://search.maven.org/artifact/com.aallam.openai/openai-client)
[![License](https://img.shields.io/github/license/Aallam/openai-kotlin?color=yellow)](LICENSE.md)
[![Kotlin](https://img.shields.io/badge/kotlin-1.7.10-a97bff.svg?logo=kotlin)](https://kotlinlang.org/docs/releases.html#release-details)
[![Documentation](https://img.shields.io/badge/docs-openai--kotlin-lightgrey)](https://mouaad.aallam.com/openai-kotlin/)

Kotlin client for [OpenAI's API](https://beta.openai.com/docs/api-reference) with multiplatform and coroutines capabilities. 

## 🛠 Setup

1. Install OpenAI API Kotlin client by adding the following dependency to your `gradle.build` file:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.aallam.openai:openai-client:$kotlin_client_version"
}
```
2. Choose and add to your dependencies one of [Ktor's engines](https://ktor.io/docs/http-client-engines.html).

> Alternatively, you can use [openai-client-bom](openai-client-bom/)

### Multiplaform
In multiplatform projects, add openai client dependency to `commonMain`, and choose an [engine](https://ktor.io/docs/http-client-engines.html) for each target.

## 💡 Getting Started

Create an instance of `OpenAI` client:
```kotlin
val openAI = OpenAI(apiKey)
```
Use your `OpenAI` instance to make API requests:

<details>
  <summary><strong>List models</strong></summary>

```kotlin
val models: List<Model> = openAI.models()
```
</details>

<details>
  <summary><strong>Retrieve an model</strong></summary>

```kotlin
val id = ModelId("text-ada-001")
val model: Model = openAI.model(id)
```
</details>    
    
<details>
  <summary><strong>Create completion</strong></summary>

```kotlin
val completionRequest = CompletionRequest(
    model = ModelId("text-ada-001"),
    prompt = "Somebody once told me the world is gonna roll me",
    echo = true
)
val completion: TextCompletion = openAI.completion(Ada, completionRequest)
```
</details>    
    
<details>
  <summary><strong>Create completion stream</strong></summary>

```kotlin
val request = CompletionRequest(
    model = ModelId("text-davinci-002"),
    prompt = "Once upon a time",
    maxTokens = 5,
    temperature = 1.0,
    topP = 1.0,
    n = 1,
    stop = listOf("\n"),
)
val completions: Flow<TextCompletion> = openAI.completions(request)
```
</details>     

<details>
  <summary><strong>Create edits</strong></summary>

```kotlin
val edit = openAI.edit(
    request = EditsRequest(
        model = ModelId("text-davinci-edit-001"),
        input = "What day of the wek is it?",
        instruction = "Fix the spelling mistakes"
    )
)
```
</details>

<details>
  <summary><strong>List files</strong></summary>

````kotlin
val files: List<File> = openAI.files()
````
</details>

<details>
  <summary><strong>Create embeddings</strong></summary>

````kotlin
val embeddings: List<Embedding> = openAI.embeddings(
    request = EmbeddingRequest(
        model = ModelId("text-similarity-babbage-001"),
        input = listOf("The food was delicious and the waiter...")
    )
)
````
</details>  

<details>
  <summary><strong>Create moderation</strong></summary>

````kotlin
val moderation = openAI.moderations(
    request = ModerationRequest(
        input = "I want to kill them."
    )
)
````
</details>  

## ℹ️ Sample apps

Sample apps are available under `sample`, please check the [README](sample/README.md) for running instructions.

## 📄 License

OpenAI Kotlin API Client is an open-sourced software licensed under the [MIT license](LICENSE.md).
