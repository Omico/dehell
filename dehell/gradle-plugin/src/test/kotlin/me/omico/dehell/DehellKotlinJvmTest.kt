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
import me.omico.dehell.serialization.internal.readJson
import org.gradle.kotlin.dsl.embeddedKotlinVersion
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Test

@Suppress("LongMethod")
class DehellKotlinJvmTest : DehellSpecification() {
    @Test
    fun `test single module project`(): Unit = runTest(
        settingsKotlinScriptContent = {
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
        },
        buildKotlinScriptContent = {
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
        },
        arguments = arrayOf(":dehellDependencyInfo"),
        result = {
            val dependencies = testProjectDirectory.resolve("build/dehell/dependencies.json")
                .readJson<DehellModuleDependencyList>()
            val aggregatedDependencies = testProjectDirectory.resolve("build/dehell/dependencies-aggregated.json")
                .readJson<DehellModuleDependencyList>()
            assertEquals(aggregatedDependencies, dependencies)
            assertEquals(
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
                ),
                aggregatedDependencies,
            )
        },
    )

    @Test
    fun `test multi-module project`(): Unit = runTest(
        settingsKotlinScriptContent = {
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
                buildKotlinScriptContent = {
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
                },
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
            val aggregatedDependencies = testProjectDirectory.resolve("main/build/dehell/dependencies-aggregated.json")
                .readJson<DehellModuleDependencyList>()
            assertEquals(
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
                ),
                aggregatedDependencies,
            )
        },
    )
}
