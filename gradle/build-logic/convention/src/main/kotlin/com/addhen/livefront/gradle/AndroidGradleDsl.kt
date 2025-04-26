// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.gradle

import com.android.build.gradle.LibraryExtension
import com.android.build.gradle.TestedExtension
import com.android.build.gradle.internal.dsl.BaseAppModuleExtension
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.dependencies

fun Project.androidApplication(action: BaseAppModuleExtension.() -> Unit) {
    extensions.configure(action)
}

fun Project.androidLibrary(action: LibraryExtension.() -> Unit) {
    extensions.configure(action)
}

fun Project.android(action: TestedExtension.() -> Unit) {
    extensions.configure(action)
}

fun Project.configureAndroid() {
    android {
        namespace?.let {
            this.namespace = it
        }
        compileSdkVersion(libs.findVersion("compileSdk").get().toString().toInt())

        defaultConfig {
            minSdk = libs.findVersion("minSdk").get().toString().toInt()

            versionCode = 1
            versionName = "0.1.0"
        }

        lintOptions {
            disable += setOf(
                "ObsoleteLintCustomCheck",
            )
        }

        compileOptions {
            isCoreLibraryDesugaringEnabled = true
        }

        dependencies {
            add("coreLibraryDesugaring", libs.findLibrary("tooling.desugaring").get())
        }
        testOptions {
            unitTests {
                isIncludeAndroidResources = true
            }
        }

        defaultConfig.targetSdk = libs.findVersion("targetSdk").get().toString().toInt()
    }
}
