/*
 * Copyright 2024 Omico
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
package me.omico.dehell.gradle.internal.services

import me.omico.dehell.gradle.internal.services.DehellDependencyService.Companion.NAME
import me.omico.dehell.serialization.DehellDependencyList
import me.omico.dehell.serialization.DehellModuleDependency
import me.omico.dehell.serialization.DehellProjectDependency
import me.omico.dehell.serialization.internal.writeJsonToFile
import org.gradle.api.invocation.Gradle
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.kotlin.dsl.registerIfAbsent
import java.io.File

internal abstract class DehellDependencyService : BuildService<BuildServiceParameters.None> {
    private val projectDependenciesMap: MutableMap<String, DehellDependencyList> = mutableMapOf()

    fun collectDependencies(projectPath: String, dependencies: DehellDependencyList) {
        projectDependenciesMap[projectPath] = dependencies
    }

    fun aggregateDependencies(projectPath: String, outputFile: File) {
        val aggregatedDependencies = mutableSetOf<DehellModuleDependency>()
        aggregateDependencies(aggregatedDependencies, projectPath)
        aggregatedDependencies.sorted().writeJsonToFile(outputFile)
    }

    private fun aggregateDependencies(aggregatedDependencies: MutableSet<DehellModuleDependency>, projectPath: String) {
        val projectDependencies = projectDependenciesMap.getOrElse(projectPath) {
            error(
                "Cannot find dependencies for project $projectPath. " +
                    "Did you forget to apply the dehell plugin in $projectPath?",
            )
        }
        projectDependencies.forEach { dependency ->
            when (dependency) {
                is DehellModuleDependency -> aggregatedDependencies.add(dependency)
                is DehellProjectDependency -> aggregateDependencies(aggregatedDependencies, dependency.path)
            }
        }
    }

    companion object {
        const val NAME: String = "DehellDependencyService"
    }
}

internal fun Gradle.registerDehellDependencyServiceIfAbsent(): Provider<DehellDependencyService> =
    sharedServices.registerIfAbsent(
        name = NAME,
        implementationType = DehellDependencyService::class,
        configureAction = {},
    )
