import org.gradle.kotlin.dsl.creating
import org.gradle.kotlin.dsl.getValue
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget

fun KotlinMultiplatformExtension.native() {
    val targets = mutableListOf<KotlinNativeTarget>().apply {
        add(linuxX64())
        add(macosX64())
        add(macosArm64())
        add(mingwX64())
    }
    sourceSets.apply {
        val nativeMain by creating { dependsOn(getByName("commonMain")) }
        val nativeTest by creating { dependsOn(getByName("commonTest")) }
        targets.forEach { target ->
            getByName("${target.name}Main").dependsOn(nativeMain)
            getByName("${target.name}Test").dependsOn(nativeTest)
        }
    }
}
