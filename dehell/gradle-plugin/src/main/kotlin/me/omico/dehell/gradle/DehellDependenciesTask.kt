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
package me.omico.dehell.gradle

import me.omico.dehell.gradle.internal.DehellDependenciesService
import org.gradle.api.DefaultTask
import org.gradle.api.Project
import org.gradle.api.artifacts.component.ModuleComponentSelector
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal
import org.gradle.api.tasks.TaskAction
import org.gradle.api.tasks.diagnostics.internal.ConfigurationFinder
import org.gradle.work.DisableCachingByDefault

@DisableCachingByDefault
abstract class DehellDependenciesTask : DefaultTask() {
    @get:Input
    internal abstract val resolvedComponentResultProperty: Property<ResolvedComponentResult>

    @get:Internal
    internal abstract val dehellDependenciesServiceProperty: Property<DehellDependenciesService>

    init {
        group = "dehell"
    }

    internal fun Project.configure(
        dehellExtension: DehellExtension,
        dehellDependenciesServiceProvider: Provider<DehellDependenciesService>,
    ) {
        usesService(dehellDependenciesServiceProvider)
        val configurationName = dehellExtension.variant
            ?.let { variant -> "${variant}CompileClasspath" }
            ?: "compileClasspath"
        val configuration = ConfigurationFinder.find(configurations, configurationName)
        resolvedComponentResultProperty.convention(configuration.incoming.resolutionResult.rootComponent)
        dehellDependenciesServiceProperty.convention(dehellDependenciesServiceProvider)
    }

    @TaskAction
    protected fun execute() {
        val dehellDependenciesService = dehellDependenciesServiceProperty.get()
        resolvedComponentResultProperty.get().dependencies
            .filterIsInstance<ResolvedDependencyResult>()
            .map(ResolvedDependencyResult::getRequested)
            .filterIsInstance<ModuleComponentSelector>()
            .forEach(dehellDependenciesService::add)
    }

    companion object {
        const val NAME = "dehellDependencies"
    }
}
