// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.gradle.plugins

import com.addhen.livefront.gradle.configureSpotless
import org.gradle.api.Plugin
import org.gradle.api.Project

@Suppress("unused")
class SpotlessConventionPlugin : Plugin<Project> {
	override fun apply(target: Project) = with(target) {
		configureSpotless()

		afterEvaluate {
			if (tasks.findByName("check")?.enabled == true) {
				tasks.getByName("check").dependsOn("spotlessCheck")
			}
		}
	}
}
