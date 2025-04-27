// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0


plugins {
    id("com.addhen.livefront.gradle.plugins.root")
    alias(libs.plugins.android.application) apply false
    alias(libs.plugins.kotlin.android) apply false
    alias(libs.plugins.android.library) apply false
    alias(libs.plugins.kotlin.compose) apply false
    alias(libs.plugins.cache.fix) apply false
    alias(libs.plugins.android.junit5) apply false
    alias(libs.plugins.spotless) apply false
    alias(libs.plugins.kotlin.serialization) apply false
    alias(libs.plugins.jetbrains.kotlin.jvm) apply false
}
