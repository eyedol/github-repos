// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.gradle

import com.diffplug.gradle.spotless.SpotlessExtension
import com.diffplug.spotless.LineEnding
import org.gradle.api.Project
import org.gradle.kotlin.dsl.configure

fun Project.configureSpotless() {
    with(pluginManager) {
        apply("com.diffplug.spotless")
    }

    spotless {
        // Workaround for https://github.com/diffplug/spotless/issues/1644
        lineEndings = LineEnding.PLATFORM_NATIVE
        val ktlintVersion = libs.findVersion("ktlint").get().requiredVersion
        kotlin {
            target("**/*.kt")
            targetExclude("${layout.buildDirectory}/**/*.kt")
            targetExclude("bin/**/*.kt")
            ktlint(ktlintVersion)
                .editorConfigOverride(
                    mapOf(
                        "ij_kotlin_imports_layout" to "*,java.**,javax.**,kotlin.**,^",
                        "android" to "true",
                    ),
                )
            trimTrailingWhitespace()
            leadingTabsToSpaces()
            endWithNewline()
            licenseHeaderFile(project.rootProject.file("spotless/copyright.txt"))
        }

        kotlinGradle {
            target("**/*.kts")
            ktlint(ktlintVersion)
            trimTrailingWhitespace()
            leadingTabsToSpaces()
            endWithNewline()
            licenseHeaderFile(rootProject.file("spotless/copyright.txt"), "(^(?![\\/ ]\\**).*$)")
        }

        format("kts") {
            target("**/*.kts")
            targetExclude("**/build/**/*.kts")
        }
        format("xml") {
            target("**/*.xml")
            targetExclude("**/build/**/*.xml", ".idea/**/*.xml")
        }
    }
}

private fun Project.spotless(
    action: SpotlessExtension.() -> Unit,
) = extensions.configure<SpotlessExtension>(action)
