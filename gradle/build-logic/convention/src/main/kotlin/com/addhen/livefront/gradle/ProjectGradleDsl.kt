// Copyright 2025, Livefront sample app project contributors
// SPDX-License-Identifier: Apache-2.0

package com.addhen.livefront.gradle

import org.gradle.api.Project
import org.gradle.api.artifacts.ExternalModuleDependencyBundle
import org.gradle.api.artifacts.MinimalExternalModuleDependency
import org.gradle.api.artifacts.VersionCatalog
import org.gradle.api.artifacts.VersionCatalogsExtension
import org.gradle.api.provider.Provider
import org.gradle.kotlin.dsl.DependencyHandlerScope
import org.gradle.kotlin.dsl.configure
import org.gradle.kotlin.dsl.getByType
import org.jetbrains.kotlin.compose.compiler.gradle.ComposeCompilerGradlePluginExtension
import java.util.Optional

val Project.libs get() = extensions.getByType<VersionCatalogsExtension>().named("libs")

fun Project.composeCompiler(block: ComposeCompilerGradlePluginExtension.() -> Unit) {
	extensions.configure<ComposeCompilerGradlePluginExtension>(block)
}

internal fun VersionCatalog.library(name: String): MinimalExternalModuleDependency {
	return findLibrary(name).get().get()
}

internal fun VersionCatalog.version(name: String): String {
	return findVersion(name).get().requiredVersion
}

fun DependencyHandlerScope.implementation(
	artifact: MinimalExternalModuleDependency,
) {
	add("implementation", artifact)
}

fun DependencyHandlerScope.lintChecks(
	artifact: MinimalExternalModuleDependency,
) {
	add("lintChecks", artifact)
}

fun DependencyHandlerScope.implementationBundle(
	artifact: Optional<Provider<ExternalModuleDependencyBundle>>,
) {
	add("implementation", artifact.get())
}

fun DependencyHandlerScope.debugImplementation(
	artifact: MinimalExternalModuleDependency,
) {
	add("debugImplementation", artifact)
}

fun DependencyHandlerScope.androidTestImplementation(
	artifact: MinimalExternalModuleDependency,
) {
	add("androidTestImplementation", artifact)
}

fun DependencyHandlerScope.testImplementation(
	artifact: MinimalExternalModuleDependency,
) {
	add("testImplementation", artifact)
}

fun DependencyHandlerScope.implementationPlatform(
	artifact: MinimalExternalModuleDependency,
) {
	add("implementation", platform(artifact))
}

fun DependencyHandlerScope.ksp(
	artifact: MinimalExternalModuleDependency,
) {
	add("ksp", artifact)
}

fun DependencyHandlerScope.kspTest(
	artifact: MinimalExternalModuleDependency,
) {
	add("kspTest", artifact)
}

private fun DependencyHandlerScope.api(
	artifact: MinimalExternalModuleDependency,
) {
	add("api", artifact)
}
