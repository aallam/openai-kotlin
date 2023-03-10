# OpenAI API client for Kotlin

[![Maven Central](https://img.shields.io/maven-central/v/com.aallam.openai/openai-client?color=blue&label=Download)](https://central.sonatype.com/namespace/com.aallam.openai)
[![License](https://img.shields.io/github/license/Aallam/openai-kotlin?color=yellow)](LICENSE.md)
[![Kotlin](https://img.shields.io/badge/kotlin-1.8.10-a97bff.svg?logo=kotlin)](https://kotlinlang.org/docs/releases.html#release-details)
[![Documentation](https://img.shields.io/badge/docs-openai--kotlin-blueviolet)](https://mouaad.aallam.com/openai-kotlin/)

Kotlin client for [OpenAI's API](https://beta.openai.com/docs/api-reference) with multiplatform and coroutines
capabilities.

## 📦 Setup

1. Install OpenAI API Kotlin client by adding the following dependency to your `gradle.build` file:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.aallam.openai:openai-client:<version>"
}
```

2. Choose and add to your dependencies one of [Ktor's engines](https://ktor.io/docs/http-client-engines.html).

#### BOM

Alternatively, you can use [openai-client-bom](/openai-client-bom)  by adding the following dependency to your `gradle.build` file

```groovy
dependencies {
    // import Kotlin API client BOM
    implementation platform('com.aallam.openai:openai-client-bom:<version>')

    // define dependencies without versions
    implementation 'com.aallam.openai:openai-client'
    implementation 'io.ktor:ktor-client-okhttp'
}
```

### Multiplaform

In multiplatform projects, add openai client dependency to `commonMain`, and choose
an [engine](https://ktor.io/docs/http-client-engines.html) for each target.

## ⚡️ Getting Started

Create an instance of `OpenAI` client:

```kotlin
val openAI = OpenAI(apiKey)
```

> ℹ️ OpenAI encourages using environment variables for the API key. [Read more](https://help.openai.com/en/articles/5112595-best-practices-for-api-key-safety).

Use your `OpenAI` instance to make API requests. [Learn more](guides/GettingStarted.md).

### Supported features

- [Models](guides/GettingStarted.md#models)
- [Completions](guides/GettingStarted.md#completions)
- [Chat](guides/GettingStarted.md#chat)
- [Edits](guides/GettingStarted.md#edits)
- [Images](guides/GettingStarted.md#images)
- [Embeddings](guides/GettingStarted.md#embeddings)
- [Files](guides/GettingStarted.md#files)
- [Fine-tunes](guides/GettingStarted.md#fine-tunes)
- [Moderations](guides/GettingStarted.md#moderations)
- [Audio](guides/GettingStarted.md#audio)

## ℹ️ Sample apps

Sample apps are available under `sample`, please check the [README](sample/README.md) for running instructions.

## 📄 License

OpenAI Kotlin API Client is an open-sourced software licensed under the [MIT license](LICENSE.md).
**This is an unofficial library, it is not affiliated with nor endorsed by OpenAI**. Contributions are welcome.
