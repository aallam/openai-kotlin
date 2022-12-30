import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.getValue
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension

fun KotlinMultiplatformExtension.native() {
    sourceSets.apply {
        // Native targets
        val nativeMain by creating { dependsOn(getByName("commonMain")) }
        val nativeTest by creating { dependsOn(getByName("commonTest")) }

        // Desktop targets
        val desktopMain by creating { dependsOn(nativeMain) }
        val desktopTest by creating { dependsOn(nativeTest) }
        listOf(linuxX64(), mingwX64()).forEach { target ->
            getByName("${target.name}Main").dependsOn(desktopMain)
            getByName("${target.name}Test").dependsOn(desktopTest)
        }

        // Darwin targets
        val darwinMain by creating { dependsOn(nativeMain) }
        val darwinTest by creating { dependsOn(nativeTest) }
        listOf(
            iosX64(),
            iosArm64(),
            macosX64(),
            macosArm64(),
            tvosX64(),
            tvosArm64(),
            watchosArm64(),
            watchosX64(),
        ).forEach { target ->
            getByName("${target.name}Main").dependsOn(darwinMain)
            getByName("${target.name}Test").dependsOn(darwinTest)
        }
    }
}

fun KotlinMultiplatformExtension.jsNode() {
    js {
        compilations.all {
            kotlinOptions {
                moduleKind = "umd"
                sourceMap = true
                metaInfo = true
            }
        }
        nodejs {
            testTask {
                useMocha {
                    timeout = "20s"
                }
            }
        }
    }
}
