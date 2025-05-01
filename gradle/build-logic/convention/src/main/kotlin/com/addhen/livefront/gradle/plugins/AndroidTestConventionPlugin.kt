// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.gradle.plugins

import com.addhen.livefront.gradle.androidTestImplementation
import com.addhen.livefront.gradle.androidTestRuntimeOnly
import com.addhen.livefront.gradle.library
import com.addhen.livefront.gradle.libs
import com.android.build.gradle.BaseExtension
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.apply
import org.gradle.kotlin.dsl.dependencies
import org.gradle.kotlin.dsl.getByType

class AndroidTestConventionPlugin : Plugin<Project> {

    override fun apply(target: Project): Unit = with(target) {
        apply<Junit5ConventionPlugin>()

        extensions.getByType<BaseExtension>().apply {
            defaultConfig {
                testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
                testInstrumentationRunnerArguments["runnerBuilder"] = "de.mannodermaus.junit5.AndroidJUnit5Builder"
            }

            testOptions {
                unitTests {
                    isIncludeAndroidResources = true
                }
            }

            packagingOptions {
                resources.excludes.add("META-INF/LICENSE*")
            }
        }

        dependencies {
            androidTestImplementation(libs.library("test-androidx-test-runner"))
            androidTestImplementation(libs.library("test-androidx-test-ext"))
            androidTestImplementation(libs.library("test-coroutines-test"))
            androidTestImplementation(libs.library("test-jupiter-api"))
            androidTestImplementation(libs.library("test-junit5-android-test-runner"))
            androidTestRuntimeOnly(libs.library("test-junit5-android-test-core"))
        }
    }
}
