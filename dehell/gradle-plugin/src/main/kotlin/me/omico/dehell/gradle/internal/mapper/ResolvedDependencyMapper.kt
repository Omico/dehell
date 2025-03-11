/*
 * Copyright 2025 Omico
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
package me.omico.dehell.gradle.internal.mapper

import me.omico.dehell.gradle.internal.DefaultMavenRepositoryMap
import me.omico.dehell.gradle.internal.DependenciesBuildOperationResult
import me.omico.dehell.gradle.internal.ResolvedDependency
import org.gradle.api.artifacts.result.ResolvedComponentResult
import org.gradle.api.artifacts.result.ResolvedDependencyResult

internal fun ResolvedComponentResult.toResolvedDependency(
    projectPath: String,
    operationResult: DependenciesBuildOperationResult,
): ResolvedDependency =
    ResolvedDependency(
        group = moduleVersion?.group ?: "unknown",
        artifact = moduleVersion?.name ?: "unknown",
        version = moduleVersion?.version ?: "unknown",
        projectPath = projectPath,
        repositoryId = operationResult.resolveRepositoryId(this),
        dependencies = run {
            dependencies
                .filterIsInstance<ResolvedDependencyResult>()
                .map { it.selected.id.displayName }
                .toSortedSet()
        },
    )

private fun DependenciesBuildOperationResult.resolveRepositoryId(component: ResolvedComponentResult): String? {
    val repositoryId = getRepositoryId(component) ?: return null
    return DefaultMavenRepositoryMap[repositoryId]?.id ?: repositoryId
}
