// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0


plugins {
    id("com.addhen.livefront.android.library")
    id("com.addhen.livefront.kotlin.android")
    id("com.addhen.livefront.hilt.android")
    id("com.addhen.livefront.serialization")
}

android.namespace = "com.addhen.livefront.data"

dependencies {
    implementation(libs.data.kotlin.serialization)
    implementation(libs.data.okhttp)
    implementation(libs.data.retrofit)
    implementation(libs.data.retrofit.converter.kotlinx.serialization)
    implementation(libs.di.hilt.android)
    implementation(libs.tooling.coroutines.android)
    implementation(libs.logging.interceptor)
    implementation(libs.logging.timber)
    ksp(libs.di.hilt.compiler)
}
