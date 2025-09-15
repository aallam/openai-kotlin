import com.diffplug.gradle.spotless.SpotlessExtension
import org.gradle.api.tasks.testing.logging.TestExceptionFormat
import org.gradle.api.tasks.testing.logging.TestLogEvent.*
import org.jetbrains.dokka.gradle.DokkaMultiModuleTask
import org.jetbrains.kotlin.gradle.dsl.JvmTarget
import org.jetbrains.kotlin.gradle.targets.js.testing.KotlinJsTest
import org.jetbrains.kotlin.gradle.targets.jvm.tasks.KotlinJvmTest
import org.jetbrains.kotlin.gradle.targets.native.tasks.KotlinNativeTest
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile

@Suppress("DSL_SCOPE_VIOLATION")
plugins {
    alias(libs.plugins.kotlin.multiplaform) apply false
    alias(libs.plugins.kotlinx.serialization) apply false
    alias(libs.plugins.kotlinx.binary.validator) apply false
    alias(libs.plugins.maven.publish) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.dokka)
    // Ensure the maven-publish plugin is applied globally
    id("maven-publish")
}

subprojects {
    apply(plugin = "com.diffplug.spotless")
    apply(plugin = "maven-publish")
    configure<SpotlessExtension> {
        kotlin {
            target("**/*.kt")
            trimTrailingWhitespace()
            endWithNewline()
        }
    }

    tasks.withType<JavaCompile> {
        sourceCompatibility = "17"
        targetCompatibility = "17"
    }

    tasks.withType<KotlinCompile> {
        compilerOptions {
            jvmTarget.set(JvmTarget.JVM_17)
        }
    }

    tasks.withType<Test> {
        testLogging {
            events(STARTED, PASSED, SKIPPED, FAILED)
            exceptionFormat = TestExceptionFormat.FULL
            showStandardStreams = false
        }
    }

    tasks.withType<KotlinJvmTest>().configureEach {
        environment("LIB_ROOT", rootDir)
    }

    tasks.withType<KotlinNativeTest>().configureEach {
        environment("SIMCTL_CHILD_LIB_ROOT", rootDir)
        environment("LIB_ROOT", rootDir)
    }

    tasks.withType<KotlinJsTest>().configureEach {
        environment("LIB_ROOT", rootDir.toString())
    }

    publishing {
        repositories {
            maven {
                url = uri("${rootProject.rootDir}/.maven")
            }
        }
    }
}



tasks.withType<DokkaMultiModuleTask>() {
    outputDirectory.set(projectDir.resolve("docs"))
}
