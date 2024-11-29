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
package me.omico.dehell.gradle.internal.tasks

import me.omico.dehell.gradle.DehellExtension
import me.omico.dehell.gradle.internal.services.DehellDependencyService
import me.omico.dehell.gradle.internal.tasks.DehellDependencyCollector.Companion.NAME
import me.omico.dehell.serialization.DehellDependencyComparator
import me.omico.dehell.serialization.DehellModuleDependency
import me.omico.dehell.serialization.internal.writeJsonToFile
import me.omico.dehell.serialization.toDehellDependency
import org.gradle.api.Project
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.diagnostics.internal.ConfigurationFinder
import org.gradle.kotlin.dsl.create

internal abstract class DehellDependencyCollector : DehellDependencyTask() {
    @get:Input
    abstract val dependencyResultsProperty: Property<ResolvedComponentResult>

    @get:OutputFile
    abstract val outputFileProperty: RegularFileProperty

    fun Project.configureDehellDependencyCollector(
        dehellExtension: DehellExtension,
        dependencyServiceProvider: Provider<DehellDependencyService>,
    ) {
        configureDehellDependencyTask(dependencyServiceProvider)
        val variant = dehellExtension.variant.getOrElse("")
        require(variant.trim() == variant) {
            "Variant name must not contain leading or trailing whitespace. You have set: \"$variant\""
        }
        val configurationName = when {
            variant.isEmpty() -> "compileClasspath"
            else -> "${variant}CompileClasspath"
        }
        val configuration = ConfigurationFinder.find(configurations, configurationName)
        dependencyResultsProperty.convention(configuration.incoming.resolutionResult.rootComponent)
        outputFileProperty.convention(dehellExtension.dependencyCollectorOutputFile)
    }

    @TaskAction
    protected fun collect() {
        val dehellDependencyService = dependencyServiceProperty.get()
        val projectPath = projectPathProperty.get()
        val dependencyResults = dependencyResultsProperty.get()
        val outputFile = outputFileProperty.get().asFile
        val dependencies = dependencyResults.dependencies
            .map { result -> result.requested.toDehellDependency() }
            .toSortedSet(DehellDependencyComparator)
            .toMutableList()
        //  Remove dependencies with an empty `version` field.
        //  If a matching dependency with the same `module`, but a non-empty `version` exists.
        val moduleDependencies = dependencies.filterIsInstance<DehellModuleDependency>()
        moduleDependencies
            .filter { dependency -> dependency.version.isEmpty() }
            .forEach { dependency ->
                val matched = moduleDependencies
                    .any { other -> dependency.module == other.module && other.version.isNotEmpty() }
                if (matched) dependencies.remove(dependency)
            }
        dependencies.writeJsonToFile(outputFile)
        dehellDependencyService.collectDependencies(projectPath, dependencies)
    }

    companion object {
        const val NAME: String = "dehellCollectDependencies"
    }
}

internal fun Project.createDehellCollectDependenciesTask(
    dehellExtension: DehellExtension,
    dependencyServiceProvider: Provider<DehellDependencyService>,
): DehellDependencyCollector =
    tasks.create<DehellDependencyCollector>(NAME) {
        configureDehellDependencyCollector(dehellExtension, dependencyServiceProvider)
    }
