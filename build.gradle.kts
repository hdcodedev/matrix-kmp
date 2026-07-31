plugins {
    alias(libs.plugins.vanniktech.mavenPublish) apply false
    alias(libs.plugins.androidApplication) apply false
    alias(libs.plugins.androidMultiplatformLibrary) apply false
    alias(libs.plugins.composeCompiler) apply false
    alias(libs.plugins.composeMultiplatform) apply false
    alias(libs.plugins.composeScreenshot) apply false
    alias(libs.plugins.kotlinMultiplatform) apply false
    alias(libs.plugins.kotlinxSerialization) apply false
    alias(libs.plugins.kmpNativeCoroutines) apply false
    alias(libs.plugins.ktlint)
    id("matrix.api-compatibility")
}

subprojects {
    apply(plugin = "org.jlleitschuh.gradle.ktlint")

    extensions.configure<org.jlleitschuh.gradle.ktlint.KtlintExtension>("ktlint") {
        android.set(true)
        ignoreFailures.set(false)
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
    dependsOn(":matrix:compileKotlinLinuxX64")
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
