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
package me.omico.dehell.gradle.internal.services

import me.omico.dehell.gradle.DehellExtension
import me.omico.dehell.gradle.internal.DependenciesBuildOperationDetails
import me.omico.dehell.gradle.internal.DependenciesBuildOperationResult
import me.omico.dehell.gradle.internal.DependencyReport
import me.omico.dehell.gradle.internal.ResolvedConfiguration
import me.omico.dehell.gradle.internal.ResolvedRepository
import me.omico.dehell.gradle.internal.mapper.toResolvedDependency
import me.omico.dehell.gradle.internal.mapper.toResolvedRepositories
import me.omico.dehell.gradle.internal.services.DehellDependencyService.Companion.NAME
import me.omico.dehell.gradle.internal.services.DehellDependencyService.Parameters
import me.omico.dehell.serialization.DehellDependencyList
import me.omico.dehell.serialization.DehellModuleDependency
import me.omico.dehell.serialization.DehellProjectDependency
import me.omico.dehell.serialization.internal.writeJsonToFile
import org.gradle.api.artifacts.component.ModuleComponentIdentifier
import org.gradle.api.artifacts.component.ProjectComponentIdentifier
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.internal.artifacts.DefaultProjectComponentIdentifier
import org.gradle.api.invocation.Gradle
import org.gradle.api.provider.Provider
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters
import org.gradle.api.tasks.InputFile
import org.gradle.internal.build.event.BuildEventListenerRegistryInternal
import org.gradle.internal.operations.BuildOperationDescriptor
import org.gradle.internal.operations.BuildOperationListener
import org.gradle.internal.operations.OperationFinishEvent
import org.gradle.internal.operations.OperationIdentifier
import org.gradle.internal.operations.OperationProgressEvent
import org.gradle.internal.operations.OperationStartEvent
import org.gradle.kotlin.dsl.registerIfAbsent
import org.gradle.kotlin.dsl.support.serviceOf
import java.io.File

internal abstract class DehellDependencyService :
    BuildService<Parameters>,
    BuildOperationListener,
    AutoCloseable {
    private val projectDependenciesMap: MutableMap<String, DehellDependencyList> = mutableMapOf()
    private val resolvedConfigurations: MutableSet<ResolvedConfiguration> = mutableSetOf()
    private val resolvedRepositories: MutableSet<ResolvedRepository> = mutableSetOf()

    final override fun started(
        buildOperation: BuildOperationDescriptor,
        startEvent: OperationStartEvent,
    ): Unit = Unit

    final override fun progress(
        operationIdentifier: OperationIdentifier,
        progressEvent: OperationProgressEvent,
    ): Unit = Unit

    final override fun finished(buildOperation: BuildOperationDescriptor, finishEvent: OperationFinishEvent) {
        extractConfigurationDependencies(
            operationDetails = buildOperation.details as? DependenciesBuildOperationDetails ?: return,
            operationResult = finishEvent.result as? DependenciesBuildOperationResult ?: return,
        )
    }

    final override fun close() {
        val dependencyReportFile = parameters.dependencyReportFileProperty.get().asFile
        val report = DependencyReport(
            repositories = resolvedRepositories.toSortedSet(),
            configurations = resolvedConfigurations.toSortedSet(),
        )
        dependencyReportFile.parentFile.mkdirs()
        report.writeJsonToFile(dependencyReportFile)
    }

    private fun extractConfigurationDependencies(
        operationDetails: DependenciesBuildOperationDetails,
        operationResult: DependenciesBuildOperationResult,
    ) {
        operationDetails.repositories?.toResolvedRepositories()?.let(resolvedRepositories::addAll)
        val rootComponent = operationResult.rootComponent
        if (rootComponent.dependencies.isEmpty()) return
        val componentId = rootComponent.id
        val rootPath = when {
            componentId is DefaultProjectComponentIdentifier -> componentId.identityPath.path
            else -> operationDetails.buildPath
        }
        val resolvedConfiguration = ResolvedConfiguration(
            projectPath = rootPath,
            configurationName = operationDetails.configurationName,
        )
        rootComponent.resolvedDependencies().forEach { dependencyComponent ->
            if (dependencyComponent.id is ProjectComponentIdentifier) return@forEach
            val dependency = dependencyComponent.toResolvedDependency(
                projectPath = rootPath,
                operationResult = operationResult,
            )
            resolvedConfiguration.allDependencies.add(dependency)
            walkComponentDependencies(
                resolvedConfiguration = resolvedConfiguration,
                operationResult = operationResult,
                component = dependencyComponent,
            )
        }
        if (resolvedConfiguration.allDependencies.isEmpty()) return
        with(resolvedConfiguration.allDependencies) {
            val sortedDependencies = sorted()
            clear()
            addAll(sortedDependencies)
        }
        resolvedConfigurations.add(resolvedConfiguration)
    }

    private fun walkComponentDependencies(
        resolvedConfiguration: ResolvedConfiguration,
        operationResult: DependenciesBuildOperationResult,
        component: ResolvedComponentResult,
    ) {
        val componentId = component.id
        if (componentId !is DefaultProjectComponentIdentifier) return
        val projectPath = componentId.identityPath.path
        component.resolvedDependencies().forEach { dependencyComponent ->
            val dependencyId = dependencyComponent.id.displayName
            if (resolvedConfiguration.allDependencies.any { it.id == dependencyId }) return@forEach
            val dependency = dependencyComponent.toResolvedDependency(
                projectPath = projectPath,
                operationResult = operationResult,
            )
            resolvedConfiguration.allDependencies.add(dependency)
            walkComponentDependencies(
                resolvedConfiguration = resolvedConfiguration,
                operationResult = operationResult,
                component = dependencyComponent,
            )
        }
    }

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

    interface Parameters : BuildServiceParameters {
        @get:InputFile
        val dependencyReportFileProperty: RegularFileProperty
    }

    companion object {
        const val NAME: String = "DehellDependencyService"
    }
}

internal fun Gradle.registerDehellDependencyServiceIfAbsent(
    dehellExtension: DehellExtension,
): Provider<DehellDependencyService> {
    val dependencyServiceProvider = sharedServices.registerIfAbsent(
        name = NAME,
        implementationType = DehellDependencyService::class,
        configureAction = {
            parameters.dependencyReportFileProperty.set(dehellExtension.dependencyReportOutputFile)
        },
    )
    serviceOf<BuildEventListenerRegistryInternal>().onOperationCompletion(dependencyServiceProvider)
    return dependencyServiceProvider
}

private fun ResolvedComponentResult.resolvedDependencies(): List<ResolvedComponentResult> =
    dependencies.filterIsInstance<ResolvedDependencyResult>()
        .map(ResolvedDependencyResult::getSelected)
        .mapNotNull(::resolvePluginMarkerIfNeeded)
        .filter { it != this }

private fun resolvePluginMarkerIfNeeded(rawComponent: ResolvedComponentResult): ResolvedComponentResult? {
    if (rawComponent.id !is ModuleComponentIdentifier) return rawComponent
    val componentId = rawComponent.id as ModuleComponentIdentifier
    if (componentId.module != componentId.group + ".gradle.plugin") return rawComponent
    if (rawComponent.dependencies.isEmpty()) return null
    if (rawComponent.dependencies.size != 1) return rawComponent
    val pluginDependency = rawComponent.dependencies.iterator().next() as? ResolvedDependencyResult
    return pluginDependency?.selected
}
