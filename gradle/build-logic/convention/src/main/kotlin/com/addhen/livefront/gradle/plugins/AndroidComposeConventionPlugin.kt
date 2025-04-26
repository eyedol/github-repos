// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.gradle.plugins

import com.addhen.livefront.gradle.android
import com.addhen.livefront.gradle.composeCompiler
import com.addhen.livefront.gradle.implementation
import com.addhen.livefront.gradle.implementationPlatform
import com.addhen.livefront.gradle.library
import com.addhen.livefront.gradle.libs
import com.addhen.livefront.gradle.lintChecks
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeFeatureFlag

@Suppress("unused")
class AndroidComposeConventionPlugin : Plugin<Project> {
    override fun apply(target: Project) = with(target) {
        with(pluginManager) {
            apply("org.jetbrains.kotlin.plugin.compose")
        }
        android {
            buildFeatures.compose = true
        }

        composeCompiler {
            featureFlags.set(setOf(ComposeFeatureFlag.OptimizeNonSkippingGroups))
        }

        dependencies {
            implementation(libs.library("presentation.androidx.compose.ui.tooling.preview"))
            implementationPlatform(libs.library("presentation.androidx.compose.bom"))
            implementation(libs.library("presentation.androidx.core.ktx"))
            implementation(libs.library("presentation.androidx.compose.ui"))
            implementation(libs.library("presentation.androidx.activity.compose"))
            implementation(libs.library("presentation.androidx.compose.material3"))
            implementation(libs.library("presentation.androidx.lifecycle.runtime.ktx"))
            implementation(libs.library("presentation.androidx.compose.material.iconsExtended"))
            implementation(libs.library("presentation.androidx.compose.materialWindow"))
            implementation(libs.library("presentation.androidx.navigation.compose"))
            implementation(libs.library("presentation.androidx.compose.animation"))
            implementation(libs.library("presentation.androidx.compose.ui"))
            implementation(libs.library("presentation.androidx.compose.runtime"))
            implementation(libs.library("presentation.androidx.compose.ui.graphics"))
            implementation(libs.library("presentation.hilt.navigation.compose"))
            lintChecks(libs.library("tooling.compose.lint.check"))
        }
    }
}
