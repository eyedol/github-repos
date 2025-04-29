// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0


plugins {
    id("com.addhen.livefront.android.application")
    id("com.addhen.livefront.kotlin.android")
    id("com.addhen.livefront.compose.android")
    id("com.addhen.livefront.hilt.android")
    id("com.addhen.livefront.serialization")
    id("com.addhen.livefront.junit5")
}

android {
    namespace = "com.addhen.livefront"

    defaultConfig {
        applicationId = "com.addhen.livefront"
        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildFeatures {
        buildConfig = true
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro",
            )
        }
    }
}

dependencies {
    implementation(projects.data)
    // Compose dependencies are in AndroidComposeConventionPlugin.kt
    implementation(libs.presentation.android.material)
    implementation(libs.presentation.androidx.appcompat)
    implementation(libs.presentation.coil.compose)
    implementation(libs.presentation.paging.runtime)
    implementation(libs.presentation.paging.compose)
    implementation(libs.logging.timber)
    implementation(libs.data.kotlin.serialization)
    // Dagger Hilt
    implementation(libs.di.hilt.android)
    implementation(libs.multidex)

    testImplementation(projects.testing)
    testImplementation(libs.test.paging.testing.android)
    ksp(libs.di.hilt.compiler)
}
