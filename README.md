# OpenAI API client for Kotlin

[![Maven Central](https://img.shields.io/maven-central/v/com.aallam.openai/openai-client?color=blue&label=Download)](https://central.sonatype.com/namespace/com.aallam.openai)
[![License](https://img.shields.io/github/license/Aallam/openai-kotlin?color=yellow)](LICENSE.md)
[![Documentation](https://img.shields.io/badge/docs-api-a97bff.svg?logo=kotlin)](https://mouaad.aallam.com/openai-kotlin/)

Kotlin client for [OpenAI's API](https://beta.openai.com/docs/api-reference) with multiplatform and coroutines
capabilities.

## 📦 Setup

1. Install OpenAI API Kotlin client by adding the following dependency to your `build.gradle` file:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.aallam.openai:openai-client:3.2.0"
}
```

2. Choose and add to your dependencies one of [Ktor's engines](https://ktor.io/docs/http-client-engines.html).

#### BOM

Alternatively, you can use [openai-client-bom](/openai-client-bom)  by adding the following dependency to your `build.gradle` file

```groovy
dependencies {
    // import Kotlin API client BOM
    implementation platform('com.aallam.openai:openai-client-bom:3.2.0')

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

In addition to creating an instance of `OpenAI` using just the API key, you can also use `OpenAIConfig` to configure the OpenAI client in a more detailed way.

```kotlin
val config = OpenAIConfig(
    token = apiKey,
    timeout = Timeout(socket = 60.seconds),
    // additional configurations...
)

val openAI = OpenAI(config)
```

> **Note**: OpenAI encourages using environment variables for the API key. [Read more](https://help.openai.com/en/articles/5112595-best-practices-for-api-key-safety).

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

## 🔒 ProGuard / R8

If you are using R8 or ProGuard add the options from this [file](openai-core/src/jvmMain/resources/META-INF/proguard/openai.pro).

## 📸 Snapshots

![Snapshot](https://img.shields.io/badge/dynamic/xml?url=https://oss.sonatype.org/service/local/repositories/snapshots/content/com/aallam/openai/openai-client/maven-metadata.xml&label=snapshot&color=red&query=.//versioning/latest)

<details>
 <summary>Learn how to import snapshot version</summary>

To import snapshot versions into your project, add the following code snippet to your gradle file:

```groovy
repositories {
   //...
   maven { url 'https://oss.sonatype.org/content/repositories/snapshots/' }
}
```

</details>

## 📄 License

OpenAI Kotlin API Client is an open-sourced software licensed under the [MIT license](LICENSE.md).
**This is an unofficial library, it is not affiliated with nor endorsed by OpenAI**. Contributions are welcome.
