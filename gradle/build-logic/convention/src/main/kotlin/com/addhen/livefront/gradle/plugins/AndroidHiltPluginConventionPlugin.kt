// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.gradle.plugins

import com.addhen.livefront.gradle.android
import com.addhen.livefront.gradle.implementation
import com.addhen.livefront.gradle.ksp
import com.addhen.livefront.gradle.kspTest
import com.addhen.livefront.gradle.library
import com.addhen.livefront.gradle.libs
import com.addhen.livefront.gradle.testImplementation
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class AndroidHiltPluginConventionPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		with(target) {
			with(pluginManager) {
				apply("com.google.devtools.ksp")
				apply("dagger.hilt.android.plugin")
			}

			android {
				packagingOptions {
					resources {
						excludes += "META-INF/gradle/incremental.annotation.processors"
					}
				}
			}
			dependencies {
				implementation(libs.library("di-hilt-android"))
				ksp(libs.library("di-hilt-compiler"))
				testImplementation(libs.library("test-di-hilt-android-testing"))
				kspTest(libs.library("test-di-hilt-android-testing"))
			}
		}
	}
}
