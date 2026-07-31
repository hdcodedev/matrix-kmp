import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidMultiplatformLibrary)
    alias(libs.plugins.vanniktech.mavenPublish)
    `signing`
}

val matrixReleaseVersion =
    providers.gradleProperty("matrixReleaseVersion").orNull?.takeIf { it.isNotBlank() }
group = "io.github.hdcodedev"
version = matrixReleaseVersion ?: "1.0.0"

kotlin {
    jvm()
    androidLibrary {
        namespace = "io.github.hdcodedev.matrix"
        compileSdk =
            libs.versions.android.compileSdk
                .get()
                .toInt()
        minSdk =
            libs.versions.android.minSdk
                .get()
                .toInt()

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
    linuxX64()

    sourceSets {
        commonMain.dependencies {
            // put your multiplatform dependencies here
        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
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
    val signingKeyId: String? = findProperty("signingKeyId") as String?
    val signingPassword: String? = findProperty("signingPassword") as String?
    val signingSecretKey: String? = findProperty("signingSecretKey") as String?

    if (signingKeyId != null && signingPassword != null && signingSecretKey != null) {
        useInMemoryPgpKeys(signingKeyId, signingSecretKey, signingPassword)
    }
}
