import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsCompose)
    alias(libs.plugins.composeCompiler)
    alias(libs.plugins.kotlinSerialization)
    kotlin("native.cocoapods")
}

kotlin {
    androidTarget {
        compilations.all {
            compileTaskProvider.configure {
                compilerOptions {
                    jvmTarget.set(JvmTarget.JVM_1_8)
                }
            }
        }
    }

    targets.withType<org.jetbrains.kotlin.gradle.plugin.mpp.KotlinNativeTarget> {
        binaries.all {
            freeCompilerArgs += listOf("-Xcache-directories=")
        }
    }

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach {
        it.binaries.framework {
            baseName = "shared"
            isStatic = true
        }
    }

    cocoapods {
        version = "1.0"
        summary = "Shared KMM Module"
        homepage = "https://example.com"
        ios.deploymentTarget = "14.0"
        framework {
            baseName = "shared"
            isStatic = true
        }
        podfile = project.file("../iosApp/Podfile")  // ← add this line
    }
    sourceSets {
        commonMain.dependencies {
            implementation(compose.ui)
            implementation(compose.foundation)
            implementation(compose.animation)
            implementation(compose.material3)
            @OptIn(org.jetbrains.compose.ExperimentalComposeLibrary::class)
            implementation(compose.components.resources)
            api(compose.materialIconsExtended)
            implementation(libs.ktor.core)
            implementation(libs.ktor.logging)
            implementation(libs.ktor.serialization)
            implementation(libs.ktor.negotiation)
            implementation(libs.kotlinx.serialization.json)
            implementation(libs.kotlinx.datetime)
            implementation(libs.androidx.datastore.preferences)
            implementation(libs.coil3.compose)  // ← only coil compose in common
            implementation(libs.precompose)
            implementation(libs.precompose.koin)
            implementation(libs.precompose.viewmodel)
            api(libs.koin.compose)
            implementation(libs.kotlinx.serialization.json.v17x)

        }

        commonTest.dependencies {
            implementation(libs.kotlin.test)
        }

        androidMain.dependencies {
            implementation(libs.ktor.okhttp)
            implementation(libs.coil3.network.okhttp)
            implementation(libs.koin.android)
        }

        iosMain.dependencies {
            implementation(libs.ktor.darwin)
            implementation(libs.coil3.network.ktor)
        }
    }
}

android {
    namespace = "com.example.kmmexlvistech"
    compileSdk = 35
    defaultConfig {
        minSdk = 24
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}