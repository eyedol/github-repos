// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

import java.io.FileInputStream
import java.util.Properties

plugins {
    id("com.addhen.livefront.android.library")
    id("com.addhen.livefront.kotlin.android")
    id("com.addhen.livefront.hilt.android")
    id("com.addhen.livefront.serialization")
    id("com.addhen.livefront.junit5")
}

android.buildFeatures {
    buildConfig = true
}

val githubApiKey: String? = getLocalProperty("GITHUB_API_KEY", project)

android.defaultConfig {
    buildConfigField("String", "GITHUB_API_KEY", "\"$githubApiKey\""
    )
}

android.namespace = "com.addhen.livefront.data"

dependencies {
    implementation(libs.presentation.paging.runtime)
    implementation(libs.data.kotlin.serialization)
    implementation(libs.data.okhttp)
    implementation(libs.data.retrofit)
    implementation(libs.data.retrofit.converter.kotlinx.serialization)
    implementation(libs.di.hilt.android)
    implementation(libs.tooling.coroutines.android)
    implementation(libs.logging.interceptor)
    implementation(libs.logging.timber)

    testImplementation(projects.testing)
    testImplementation(libs.test.okhttp.mockwebserver)
    testImplementation(libs.test.paging.testing.android)

    ksp(libs.di.hilt.compiler)
}

fun getLocalProperty(key: String, project: Project): String? {
    val properties = Properties()
    val localPropertiesFile = project.rootProject.file("local.properties")
    if (localPropertiesFile.exists()) {
        FileInputStream(localPropertiesFile).use { fileInputStream ->
            properties.load(fileInputStream)
        }
    }
    return properties.getProperty(key)
}
