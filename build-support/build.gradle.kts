plugins {
    `kotlin-dsl`
    `java-gradle-plugin`
}

repositories {
    mavenCentral()
}

dependencies {
    compileOnly(kotlin("gradle-plugin"))
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
