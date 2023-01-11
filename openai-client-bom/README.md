# OpenAI API Kotlin Client BOM

Client [BOM][1] module, can be used to control the dependency versions of direct and transitive dependencies:

```groovy
dependencies {
    // import Kotlin API client BOM
    implementation platform('com.aallam.openai:openai-client-bom:<version>')

    // define dependencies without versions
    implementation 'com.aallam.openai:openai-client'
    implementation 'io.ktor:ktor-client-okhttp'
}
```

[1]: https://docs.gradle.org/current/userguide/platforms.html#sub:bom_import
