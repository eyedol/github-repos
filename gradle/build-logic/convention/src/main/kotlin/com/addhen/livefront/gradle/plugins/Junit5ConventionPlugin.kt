package com.addhen.livefront.gradle.plugins

import com.addhen.livefront.gradle.androidTestImplementation
import com.addhen.livefront.gradle.androidTestRuntimeOnly
import com.addhen.livefront.gradle.library
import com.addhen.livefront.gradle.libs
import com.addhen.livefront.gradle.testImplementation
import com.addhen.livefront.gradle.testRuntimeOnly
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.dependencies

class Junit5ConventionPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = with(target) {
        with(plugins) {
            apply("de.mannodermaus.android-junit5")
        }

        dependencies {
            testImplementation(libs.library("test-jupiter-api"))
            testRuntimeOnly(libs.library("test-jupiter-engine"))
            androidTestImplementation(libs.library("test-junit5-android-test-core"))
            androidTestRuntimeOnly(libs.library("test-junit5-android-test-runner"))
        }
    }
}