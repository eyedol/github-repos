// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0


plugins {
    id("com.addhen.livefront.android.library")
    id("com.addhen.livefront.kotlin.android")
    id("com.addhen.livefront.junit5")
}

android.namespace = "com.addhen.livefront.testing"

dependencies {
    implementation(libs.test.jupiter.api)
    implementation(libs.test.jupiter.engine)
    implementation(libs.lifecycle.viewmodel.savedstate.android)
    implementation(libs.presentation.androidx.navigation.compose)
    implementation(libs.test.mockk)
    implementation(libs.test.mockk.android)
    implementation(libs.test.mockk.agent)
    api(libs.test.coroutines.test)
    api(libs.test.turbine)
}
