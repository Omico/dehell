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
package me.omico.dehell.gradle.internal.tasks

import me.omico.dehell.gradle.DehellExtension
import me.omico.dehell.gradle.internal.services.DehellDependencyService
import me.omico.dehell.gradle.internal.tasks.DehellDependencyAggregator.Companion.NAME
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.TaskProvider
import org.gradle.kotlin.dsl.register

internal abstract class DehellDependencyAggregator : DehellDependencyTask() {
    @get:OutputFile
    abstract val outputFileProperty: RegularFileProperty

    fun Project.configureDehellDependencyAggregator(
        dehellExtension: DehellExtension,
        dependencyServiceProvider: Provider<DehellDependencyService>,
    ) {
        configureDehellDependencyTask(dependencyServiceProvider)
        outputFileProperty.convention(dehellExtension.dependencyAggregatorOutputFile)
    }

    @TaskAction
    protected fun aggregate() {
        val projectPath = projectPathProperty.get()
        val dependencyAggregatorOutputFile = outputFileProperty.get().asFile
        dehellDependencyService.aggregateDependencies(projectPath, dependencyAggregatorOutputFile)
    }

    companion object {
        const val NAME: String = "dehellAggregateDependencies"
    }
}

internal fun Project.registerDehellDependencyAggregatorTask(
    dehellExtension: DehellExtension,
    dependencyServiceProvider: Provider<DehellDependencyService>,
): TaskProvider<DehellDependencyAggregator> =
    tasks.register<DehellDependencyAggregator>(NAME) {
        configureDehellDependencyAggregator(dehellExtension, dependencyServiceProvider)
    }
