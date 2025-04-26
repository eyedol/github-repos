// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.gradle.plugins

import com.addhen.livefront.gradle.configureKotlin
import com.addhen.livefront.gradle.configureSpotless
import org.gradle.api.Plugin
import org.gradle.api.Project

class KotlinAndroidConventionPlugin : Plugin<Project> {
	override fun apply(target: Project) {
		with(target) {
			with(pluginManager) {
				apply("org.jetbrains.kotlin.android")
			}

			configureSpotless()
			configureKotlin()
		}
	}
}
