import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.vanniktech.mavenPublish)
    `signing`
}

group = "io.github.hdcodedev"

kotlin {
    jvm()
    android {
        namespace = "io.github.hdcodedev.matrix"
        compileSdk =
            libs.versions.android.compileSdk
                .get()
                .toInt()
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()
        androidResources.enable = true

        withJava() // enable java compilation support
        withHostTestBuilder {}.configure {}
        withDeviceTestBuilder {
            sourceSetTreeName = "test"
        }

        compilerOptions {
            jvmTarget = JvmTarget.JVM_11
        }
    }
    iosArm64()
    iosSimulatorArm64()

    sourceSets {
        commonMain.dependencies {
            implementation(libs.compose.runtime)
            implementation(libs.compose.foundation)
            implementation(libs.compose.material3)
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
            implementation(libs.compose.mpp.ui.test)
        }

        androidMain.dependencies {
            implementation(libs.androidx.compose.ui.tooling.preview)
            implementation(libs.compose.ui.tooling)
        }

        jvmMain.dependencies {
            implementation(compose.desktop.currentOs)
        }
    }
}

mavenPublishing {
    publishToMavenCentral()

    signAllPublications()

    coordinates(group.toString(), "matrix", version.toString())

    pom {
        name = "matrix-kmp"
        description = "A Kotlin Multiplatform library."
        inceptionYear = "2024"
        url = "https://github.com/hdcodedev/matrix-kmp"
        licenses {
            license {
                name = "Apache License 2.0"
                url = "https://www.apache.org/licenses/LICENSE-2.0"
                distribution = "repo"
            }
        }
        developers {
            developer {
                id = "hdcodedev"
                name = "hdcodedev"
                url = "https://github.com/hdcodedev"
            }
        }
        scm {
            url = "https://github.com/hdcodedev/matrix-kmp"
            connection = "https://github.com/hdcodedev/matrix-kmp.git"
            developerConnection = "https://github.com/hdcodedev/matrix-kmp.git"
        }
    }
}

signing {
    val signingKeyId: String? = findProperty("signingInMemoryKeyId") as String?
    val signingPassword: String? = findProperty("signingInMemoryKeyPassword") as String?
    val signingSecretKey: String? = findProperty("signingInMemoryKey") as String?

    if (signingKeyId != null && signingPassword != null && signingSecretKey != null) {
        useInMemoryPgpKeys(signingKeyId, signingSecretKey, signingPassword)
    }
}
