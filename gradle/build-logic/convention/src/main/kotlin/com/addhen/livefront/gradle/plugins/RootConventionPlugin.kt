// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.gradle.plugins

import com.addhen.livefront.gradle.configureSpotless
import org.gradle.api.Plugin
import org.gradle.api.Project

class RootConventionPlugin : Plugin<Project> {
	override fun apply(target: Project) = with(target) {
		configureSpotless()
	}
}
