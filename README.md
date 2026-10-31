# OpenAI API client for Kotlin

[![Maven Central](https://img.shields.io/maven-central/v/com.aallam.openai/openai-client?color=blue&label=Download)](https://central.sonatype.com/namespace/com.aallam.openai)
[![License](https://img.shields.io/github/license/Aallam/openai-kotlin?color=yellow)](LICENSE.md)
[![Documentation](https://img.shields.io/badge/docs-api-a97bff.svg?logo=kotlin)](https://mouaad.aallam.com/openai-kotlin/)

Kotlin client for [OpenAI's API](https://platform.openai.com/docs/api-reference) with multiplatform and coroutines
capabilities.

## 📦 Setup

1. Install OpenAI API Kotlin client by adding the following dependency to your `build.gradle` file:

```groovy
repositories {
    mavenCentral()
}

dependencies {
    implementation "com.aallam.openai:openai-client:4.1.0"
}
```

2. Choose and add to your dependencies one of [Ktor's engines](https://ktor.io/docs/http-client-engines.html).

#### BOM

Alternatively, you can use [openai-client-bom](/openai-client-bom)  by adding the following dependency to your `build.gradle` file

```groovy
dependencies {
    // import Kotlin API client BOM
    implementation platform('com.aallam.openai:openai-client-bom:4.1.0')

    // define dependencies without versions
    implementation 'com.aallam.openai:openai-client'
    runtimeOnly 'io.ktor:ktor-client-okhttp'
}
```

### Multiplatform

In multiplatform projects, add openai client dependency to `commonMain`, and choose
an [engine](https://ktor.io/docs/http-client-engines.html) for each target.

### Maven

Gradle is required for multiplatform support, but there's nothing stopping you from using the jvm client in a Maven
project. You still need to add to your dependencies one
of [Ktor's engines](https://ktor.io/docs/http-client-engines.html).

<details>
 <summary>Setup the client with maven</summary>

```xml
<dependencies>
    <dependency>
        <groupId>com.aallam.openai</groupId>
        <artifactId>openai-client-jvm</artifactId>
        <version>4.1.0</version>
    </dependency>
            
    <dependency>
        <groupId>io.ktor</groupId>
        <artifactId>ktor-client-okhttp-jvm</artifactId>
        <version>3.0.0</version>
        <scope>runtime</scope>
    </dependency>
</dependencies>
```

</details>

The BOM is not supported for Maven projects.

## ⚡️ Getting Started

> [!NOTE]
> OpenAI encourages using environment variables for the API key.
> [Read more](https://help.openai.com/en/articles/5112595-best-practices-for-api-key-safety).

Create an instance of `OpenAI` client:

```kotlin
val openai = OpenAI(
    token = "your-api-key",
    timeout = Timeout(socket = 60.seconds),
    // additional configurations...
)
```

Or you can create an instance of `OpenAI` using a pre-configured `OpenAIConfig`:

```kotlin
val config = OpenAIConfig(
    token = apiKey,
    timeout = Timeout(socket = 60.seconds),
    // additional configurations...
)

val openAI = OpenAI(config)
```

Use your `OpenAI` instance to make API requests. [Learn more](guides/GettingStarted.md).

### Supported features

- [Responses](guides/GettingStarted.md#responses)
- [Decisions](guides/Decisions.md)
- [Conversations](guides/Conversations.md)
- [Models](guides/GettingStarted.md#models)
- [Chat](guides/GettingStarted.md#chat)
- [Images](guides/Images.md)
- [Embeddings](guides/GettingStarted.md#embeddings)
- [Files](guides/GettingStarted.md#files)
- [Multipart uploads](guides/Uploads.md)
- [Fine-tuning](guides/GettingStarted.md#fine-tuning)
- [Moderations](guides/GettingStarted.md#moderations)
- [Audio](guides/GettingStarted.md#audio)
- [Realtime](guides/Realtime.md)
- [Batch](guides/GettingStarted.md#batch)
- [Vector Stores](guides/GettingStarted.md#vector-stores)
- [Webhooks](guides/Webhooks.md)
- [Organization usage and costs](guides/Administration.md)
- [OrganizationAdministration](guides/OrganizationAdministration.md)
- [Organization governance](guides/OrganizationGovernance.md)
- [Containers](guides/Containers.md)
- [Skills](guides/Skills.md)
- [Voice management](guides/VoiceManagement.md)
- [Live](guides/Live.md)
- [Spend controls](guides/SpendControls.md)
- [Safety and content provenance](guides/SafetyAndProvenance.md)
- [Fine-tuning operations](guides/FineTuningOperations.md)

#### Retired upstream APIs

The following interfaces remain available for existing consumers, but their OpenAI endpoints have shut down. See the [migration guide](guides/Migration.md).

- [Assistants](guides/GettingStarted.md#assistants)
- [Threads](guides/GettingStarted.md#threads)
- [Messages](guides/GettingStarted.md#messages)
- [Runs](guides/GettingStarted.md#runs)
- [Fine-tunes](guides/GettingStarted.md#fine-tunes)
- [Edits](guides/GettingStarted.md#edits)

Vector Stores remain independent of the retired Assistants endpoints.

#### Legacy

- [Completions](guides/GettingStarted.md#completions) is a legacy API; model availability is separate from endpoint support.


*Looking for a tokenizer? Try [ktoken](https://github.com/aallam/ktoken), a Kotlin library for tokenizing text.*

## 📚 Guides

Get started and understand more about how to use OpenAI API client for Kotlin with these guides:

- [Getting Started](guides/GettingStarted.md)
- [Chat & Tool Calls](guides/ChatToolCalls.md)
- [FileSource Guide](guides/FileSource.md)
- [API migration](guides/Migration.md)
- [Archived Assistants guide](guides/Assistants.md)

## ℹ️ Sample apps

Sample apps are available under `sample`, please check the [README](sample/README.md) for running instructions.

## 🔒 ProGuard / R8

The specific rules are [already bundled](openai-core/src/jvmMain/resources/META-INF/proguard/openai.pro) into the Jar which can be interpreted by R8 automatically.

## 📸 Snapshots

[![Snapshot](https://img.shields.io/badge/dynamic/xml?url=https://central.sonatype.com/repository/maven-snapshots/com/aallam/openai/openai-client/maven-metadata.xml&label=snapshot&color=red&query=.//versioning/latest)](https://central.sonatype.com/repository/maven-snapshots/com/aallam/openai/openai-client/)

<details>
 <summary>Learn how to import snapshot version</summary>

To import snapshot versions into your project, add the following code snippet to your gradle file:

```groovy
repositories {
   //...
   maven { url 'https://central.sonatype.com/repository/maven-snapshots/' }
}
```

</details>

## 🛠️ Troubleshooting

For common issues and their solutions, check the [Troubleshooting Guide](TROUBLESHOOTING.md).

## 🧪 Testing

`openai-client` tests are live integration tests and can generate billable API traffic.

- Default (non-billable): live tests are disabled.
- Opt-in live tests: set `OPENAI_LIVE_TESTS=1` and `OPENAI_API_KEY`.

The JVM live suite excludes mocked offline fixtures. CI runs those once in Offline Checks, then runs the live suite plus the separate Live and Content Provenance tasks. Containers live tests remain opt-in for eligible projects.

Examples:

```bash
# Free/offline checks
./gradlew :openai-core:jvmTest :openai-core:jsTest :openai-core:wasmJsTest :openai-core:apiCheck :openai-client:apiCheck

# Mocked HTTP client tests (no credentials or API traffic)
./gradlew :openai-client:offlineJvmTest

# Live smoke (billable)
OPENAI_LIVE_TESTS=1 OPENAI_API_KEY=... ./gradlew :openai-client:jvmTest --tests "*.TestModels"
```

## ⭐️ Support

Appreciate the project? Here's how you can help:

1. **Star**: Give it a star at the top right. It means a lot!
2. **Contribute**: Found an issue or have a feature idea? Submit a PR.
3. **Feedback**: Have suggestions? Open an issue or start a discussion.

## 📄 License

OpenAI Kotlin API Client is an open-sourced software licensed under the [MIT license](LICENSE.md).
**This is an unofficial library, it is not affiliated with nor endorsed by OpenAI**. Contributions are welcome.
