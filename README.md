# OpenAI Kotlin API Client

[![Maven Central](https://img.shields.io/maven-central/v/com.aallam.openai/openai-client?color=blue&label=Download)](https://search.maven.org/artifact/com.aallam.openai/openai-client)
[![License](https://img.shields.io/github/license/Aallam/openai-kotlin?color=yellow)](LICENSE.md)
[![Kotlin](https://img.shields.io/badge/kotlin-1.6.21-blue.svg?logo=kotlin)](https://kotlinlang.org/docs/releases.html#release-details)

Kotlin client for [OpenAI's API](https://beta.openai.com/docs/api-reference) with multiplatform and coroutines capabilities. 

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

<details>
  <summary><strong>List engines</strong></summary>

```kotlin
val engines: List<Engine> = openAI.engines()
```
</details>

<details>
  <summary><strong>Retrieve an engine</strong></summary>

```kotlin
val engines: List<Engine> = openAI.engines()
```
</details>    
    
<details>
  <summary><strong>Create completion</strong></summary>

```kotlin
val completionRequest = CompletionRequest(
    prompt = "Somebody once told me the world is gonna roll me",
    echo = true
)
val completion: TextCompletion = openAI.completion(Ada, completionRequest)
```
</details>    
    
<details>
  <summary><strong>Create completion stream</strong></summary>

```kotlin
val completions: Flow<TextCompletion> = openAI.completions(Ada, completionRequest)
```
</details>     

<details>
  <summary><strong>Create edits</strong></summary>

```kotlin
val request = EditsRequest(
    input = "What day of the wek is it?",
    instruction = "Fix the spelling mistakes"
)
val edit = openAI.edit(EngineId("text-davinci-edit-001"), request)
```
</details>

<details>
  <summary><strong>List files</strong></summary>

````kotlin
val files = openAI.files()
````
</details>

<details>
  <summary><strong>Create embeddings</strong></summary>

````kotlin
val embeddings = openAI.embeddings(
    engineId = EngineId("text-similarity-babbage-001"),
    request = EmbeddingRequest(input = listOf("The food was delicious and the waiter..."))
)
````
</details>  

## ℹ️ Sample apps

Sample apps are available under `sample`, please check the [README](sample/README.md) for running instructions.

## 📄 License

OpenAI Kotlin API Client is an open-sourced software licensed under the [MIT license](LICENSE.md).
