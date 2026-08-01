import org.gradle.api.Task
import org.jetbrains.kotlin.gradle.dsl.KotlinMultiplatformExtension
import org.jetbrains.kotlin.gradle.plugin.mpp.DisableCacheInKotlinVersion
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeCacheApi
import org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget
import org.jetbrains.kotlin.gradle.plugin.mpp.TestExecutable

plugins {
    alias(libs.plugins.vanniktech.mavenPublish) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeScreenshot) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinxSerialization) apply false
    alias(libs.plugins.ktlint)
    alias(libs.plugins.axionRelease)
    id("matrix.api-compatibility")
}

scmVersion {
    tag {
        prefix.set("")
        initialVersion { _, _ -> "1.0.0" }
    }
    versionIncrementer("incrementPatch")
}

val matrixReleaseVersion = providers.gradleProperty("matrixReleaseVersion").orNull?.takeIf { it.isNotBlank() }
val isCompositeIncludedBuild = gradle.parent != null
version =
    when {
        matrixReleaseVersion != null -> matrixReleaseVersion
        isCompositeIncludedBuild -> "dev-local"
        else -> scmVersion.version
    }

fun Task.isGradleSignTask(): Boolean {
    var taskClass: Class<*>? = javaClass
    while (taskClass != null) {
        if (taskClass.name == "org.gradle.plugins.signing.Sign") return true
        taskClass = taskClass.superclass
    }
    return false
}

val verifySigningKey =
    tasks.register<Exec>("verifySigningKey") {
        group = "verification"
        description = "Verifies the in-memory PGP signing key before signing."
        commandLine(
            "bash",
            layout.projectDirectory
                .file(".github/scripts/verify-signing-key.sh")
                .asFile
                .absolutePath,
            "--required",
        )
    }

@OptIn(KotlinNativeCacheApi::class)
subprojects {
    version = rootProject.version

    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    extensions.configure<org.jlleitschuh.gradle.ktlint.KtlintExtension>("ktlint") {
        android.set(true)
        ignoreFailures.set(false)
    }

    plugins.withId("signing") {
        tasks.configureEach {
            if (isGradleSignTask()) {
                dependsOn(verifySigningKey)
            }
        }
    }

    // Hosted macOS runners can provide Kotlin/Native caches built against a
    // newer simulator SDK than the deployment target used by these tests.
    plugins.withId("org.jetbrains.kotlin.multiplatform") {
        if (path != ":matrix") return@withId

        extensions.configure<KotlinMultiplatformExtension>("kotlin") {
            targets.withType<KotlinNativeTarget>().configureEach {
                if (name != "iosSimulatorArm64") return@configureEach

                binaries.withType<TestExecutable>().configureEach {
                    disableNativeCache(
                        version = DisableCacheInKotlinVersion.`2_4_10`,
                        reason =
                            "Hosted macOS caches can target a newer simulator SDK than the test deployment target.",
                    )
                }
            }
        }
    }
}

tasks.register<Exec>("buildSrcKtlintCheck") {
    group = "verification"
    description = "Runs ktlintCheck for buildSrc Kotlin code and scripts"
    commandLine("./gradlew", "-p", "buildSrc", "ktlintCheck")
}

tasks.named("ktlintCheck").configure {
    dependsOn("buildSrcKtlintCheck")
}

tasks.register("matrixTestJvm") {
    group = "verification"
    description = "Runs JVM tests for the matrix module"
    dependsOn(":matrix:jvmTest")
}

tasks.register("ciTestJvm") {
    group = "CI"
    description = "CI entry point for JVM tests"
    dependsOn("matrixTestJvm")
}

tasks.register("ciTestAndroid") {
    group = "CI"
    description = "CI entry point for Android instrumented tests"
    dependsOn(":matrix:connectedAndroidTest")
}

tasks.register("ciTestIos") {
    group = "CI"
    description = "CI entry point for iOS simulator tests"
    dependsOn(":matrix:iosSimulatorArm64Test")
}

tasks.register("ciCompile") {
    group = "CI"
    description = "Compiles all CI KMP targets without packaging outputs"
    dependsOn(":matrix:compileKotlinJvm")
    dependsOn(":matrix:compileKotlinIosArm64")
    dependsOn(":matrix:compileKotlinIosSimulatorArm64")
    dependsOn(":matrix:compileAndroidMain")
    dependsOn(":sample:shared:compileKotlinIosArm64")
    dependsOn(":sample:shared:compileKotlinIosSimulatorArm64")
    dependsOn(":sample:shared:compileAndroidMain")
}

tasks.register("ciAssemble") {
    group = "CI"
    description = "Assembles CI validation artifacts using dev/debug outputs"
    dependsOn(":matrix:jvmJar")
    dependsOn(":matrix:assembleAndroidMain")
}
