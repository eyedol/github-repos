// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0


plugins {
    `kotlin-dsl`
    alias(libs.plugins.spotless)
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(libs.versions.jdk.get()))
    }
}

spotless {
    kotlin {
        target("src/**/*.kt")
        ktlint(libs.versions.ktlint.get())
        licenseHeaderFile(rootProject.file("../../spotless/copyright.txt"))
    }
    kotlinGradle {
        target("*.kts")
        ktlint(libs.versions.ktlint.get())
        licenseHeaderFile(rootProject.file("../../spotless/copyright.txt"), "(^(?![\\/ ]\\**).*$)")
    }
}

dependencies {
    compileOnly(libs.build.logic.android.gradlePlugin)
    compileOnly(libs.build.logic.kotlin.gradlePlugin)
    compileOnly(libs.build.logic.spotless.gradlePlugin)
    compileOnly(libs.build.logic.composeCompiler.gradlePlugin)
    compileOnly(libs.build.logic.kotlinxSerializationGradlePlugin)
    implementation(libs.build.logic.kspGradlePlugin)
    implementation(libs.build.logic.hiltGradlePlugin)
}

gradlePlugin {
    plugins {

        register("root") {
            id = "com.addhen.livefront.gradle.plugins.root"
            implementationClass = "com.addhen.livefront.gradle.plugins.RootConventionPlugin"
        }

        register("androidApplication") {
            id = "com.addhen.livefront.android.application"
            implementationClass = "com.addhen.livefront.gradle.plugins.AndroidApplicationConventionPlugin"
        }

        register("androidLibrary") {
            id = "com.addhen.livefront.android.library"
            implementationClass = "com.addhen.livefront.gradle.plugins.AndroidLibraryConventionPlugin"
        }

        register("kotlinAndroid") {
            id = "com.addhen.livefront.kotlin.android"
            implementationClass = "com.addhen.livefront.gradle.plugins.KotlinAndroidConventionPlugin"
        }

        register("hitlAndroid") {
            id = "com.addhen.livefront.hilt.android"
            implementationClass = "com.addhen.livefront.gradle.plugins.AndroidHiltPluginConventionPlugin"
        }

        register("composeAndroid") {
            id = "com.addhen.livefront.compose.android"
            implementationClass = "com.addhen.livefront.gradle.plugins.AndroidComposeConventionPlugin"
        }

        register("spotless") {
            id = "com.addhen.livefront.gradle.plugins.spotless"
            implementationClass = "com.addhen.livefront.gradle.plugins.SpotlessConventionPlugin"
        }

        register("kotlinKotlinSerialization") {
            id = "com.addhen.livefront.serialization"
            implementationClass = "com.addhen.livefront.gradle.plugins.KotlinSerializationConventionPlugin"
        }

        register("junit5") {
            id = "com.addhen.livefront.junit5"
            implementationClass = "com.addhen.livefront.gradle.plugins.Junit5ConventionPlugin"
        }

        register("androidTest") {
            id = "com.addhen.livefront.android.test"
            implementationClass = "com.addhen.livefront.gradle.plugins.AndroidTestConventionPlugin"
        }
    }
}
