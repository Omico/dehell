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
import me.omico.dehell.gradle.internal.DependenciesBuildOperationRepository
import me.omico.dehell.gradle.internal.ResolvedMavenRepository
import me.omico.dehell.gradle.internal.ResolvedRepository
import me.omico.dehell.internal.removeTrailingSlash
import org.gradle.api.internal.artifacts.dsl.DefaultRepositoryHandler

internal fun DependenciesBuildOperationRepository.toResolvedRepository(): ResolvedRepository? =
    when (type) {
        "MAVEN" -> when (id) {
            // Check if it is Maven Local first.
            DefaultRepositoryHandler.DEFAULT_MAVEN_LOCAL_REPO_NAME -> ResolvedMavenRepository.MavenLocal

            else -> {
                // We use the url to identify the repository, instead of the id,
                // because the id may change in some cases.
                val url = properties["URL"]?.toString()?.removeTrailingSlash()
                DefaultMavenRepositoryMap.values.find { it.url == url } ?: ResolvedMavenRepository(id = id, url = url)
            }
        }

        else -> null
    }

internal fun List<DependenciesBuildOperationRepository>.toResolvedRepositories(): List<ResolvedRepository> =
    mapNotNull(DependenciesBuildOperationRepository::toResolvedRepository)
