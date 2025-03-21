/*
 * Copyright 2024-2025 Omico
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     https://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package me.omico.dehell

import me.omico.dehell.serialization.DehellModuleDependency
import me.omico.dehell.serialization.DehellModuleDependencyList
import org.gradle.kotlin.dsl.embeddedKotlinVersion
import org.gradle.testkit.runner.BuildResult
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

@Suppress("LongMethod")
class DehellKotlinJvmTest : DehellSpecification() {
    @Test
    fun `test single module project`(): Unit = runJvmTest(
        arguments = arrayOf(":dehellDependencyInfo"),
        result = {
            val aggregatedDependencies = actualAggregatedDependencies()
            assertEquals(
                expected = aggregatedDependencies,
                actual = actualDependencies(),
            )
            assertEquals(
                expected = DEFAULT_EXPECTED_DEPENDENCIES,
                actual = aggregatedDependencies,
            )
        },
    )

    @Test
    fun `test multi-module project`(): Unit = runJvmTest(
        gradleKotlinSettingsScriptContent = {
            // language=kotlin
            """
            |pluginManagement {
            |    repositories {
            |        mavenLocal()
            |        gradlePluginPortal()
            |    }
            |}
            |
            |dependencyResolutionManagement {
            |    repositories {
            |        mavenCentral()
            |    }
            |}
            |
            |include(":core", ":main")
            """.trimMargin()
        },
        submodules = {
            submodule(
                name = "core",
                buildKotlinScriptContent = { DEFAULT_GRADLE_KOTLIN_BUILD_SCRIPT_CONTENT },
            )
            submodule(
                name = "main",
                buildKotlinScriptContent = {
                    // language=kotlin
                    """
                    |plugins {
                    |    embeddedKotlin("jvm")
                    |    id("me.omico.dehell")
                    |}
                    |
                    |dependencies {
                    |    implementation(project(":core"))
                    |}
                    """.trimMargin()
                },
            )
        },
        arguments = arrayOf(":main:dehellDependencyInfo"),
        result = {
            assertEquals(
                expected = DEFAULT_EXPECTED_DEPENDENCIES,
                actual = actualAggregatedDependencies("main"),
            )
        },
    )

    @Test
    fun `test custom output file location`(): Unit = runJvmTest(
        gradleKotlinBuildScriptContent = {
            // language=kotlin
            """
            |plugins {
            |    embeddedKotlin("jvm")
            |    id("me.omico.dehell")
            |}
            |
            |dehell {
            |    dependencyCollectorOutputFile = file("dependencies.json")
            |    dependencyAggregatorOutputFile = file("dependencies-aggregated.json")
            |    dependencyInfoGeneratorOutputFile = file("dependencies-info.json")
            |}
            |
            |dependencies {
            |    implementation(kotlin("stdlib"))
            |    implementation(kotlin("reflect"))
            |}
            """.trimMargin()
        },
        result = {
            assertTrue(actual = testProjectDirectory.resolve("dependencies.json").exists())
            assertTrue(actual = testProjectDirectory.resolve("dependencies-aggregated.json").exists())
            assertTrue(actual = testProjectDirectory.resolve("dependencies-info.json").exists())
        },
    )

    private fun runJvmTest(
        gradleKotlinSettingsScriptContent: () -> String = { DEFAULT_GRADLE_KOTLIN_SETTINGS_SCRIPT_CONTENT },
        gradleKotlinBuildScriptContent: () -> String = { DEFAULT_GRADLE_KOTLIN_BUILD_SCRIPT_CONTENT },
        noConfigurationCache: Boolean = true,
        vararg arguments: String = arrayOf(":dehellDependencyInfo"),
        submodules: SubmoduleCreator.() -> Unit = {},
        result: BuildResult.() -> Unit,
    ): Unit =
        runTest(
            gradleKotlinSettingsScriptContent = gradleKotlinSettingsScriptContent,
            gradleKotlinBuildScriptContent = gradleKotlinBuildScriptContent,
            noConfigurationCache = noConfigurationCache,
            arguments = arguments,
            submodules = submodules,
            result = result,
        )
}

private val DEFAULT_GRADLE_KOTLIN_SETTINGS_SCRIPT_CONTENT: String =
    // language=kotlin
    """
    |pluginManagement {
    |    repositories {
    |        mavenLocal()
    |        gradlePluginPortal()
    |    }
    |}
    |
    |dependencyResolutionManagement {
    |    repositories {
    |        mavenCentral()
    |    }
    |}
    """.trimMargin()

private val DEFAULT_GRADLE_KOTLIN_BUILD_SCRIPT_CONTENT: String =
    // language=kotlin
    """
    |plugins {
    |    embeddedKotlin("jvm")
    |    id("me.omico.dehell")
    |}
    |
    |dependencies {
    |    implementation(kotlin("stdlib"))
    |    implementation(kotlin("reflect"))
    |}
    """.trimMargin()

private val DEFAULT_EXPECTED_DEPENDENCIES: DehellModuleDependencyList =
    listOf(
        DehellModuleDependency(
            group = "org.jetbrains.kotlin",
            name = "kotlin-reflect",
            version = embeddedKotlinVersion,
        ),
        DehellModuleDependency(
            group = "org.jetbrains.kotlin",
            name = "kotlin-stdlib",
            version = embeddedKotlinVersion,
        ),
    )
