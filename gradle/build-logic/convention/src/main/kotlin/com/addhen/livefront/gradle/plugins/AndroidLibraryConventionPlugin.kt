// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.gradle.plugins

import com.addhen.livefront.gradle.androidLibrary
import com.addhen.livefront.gradle.configureAndroid
import org.gradle.api.Plugin
import org.gradle.api.Project

class AndroidLibraryConventionPlugin : Plugin<Project> {
	override fun apply(target: Project) = with(target) {
		with(pluginManager) {
			apply("com.android.library")
			apply("org.gradle.android.cache-fix")
		}

		androidLibrary {
			configureAndroid()
		}
	}
}
