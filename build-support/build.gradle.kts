plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(kotlin("gradle-plugin", "2.0.0"))
    compileOnly(kotlin("gradle-plugin-api"))
}

gradlePlugin {
    plugins {
        create("build-support") {
            id = "build-support"
            implementationClass = "BuildSupport"
        }
    }
}
