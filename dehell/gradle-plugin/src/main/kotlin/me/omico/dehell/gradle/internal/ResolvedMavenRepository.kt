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
package me.omico.dehell.gradle.internal

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import me.omico.dehell.internal.removeTrailingSlash
import org.gradle.api.artifacts.ArtifactRepositoryContainer
import org.gradle.api.internal.artifacts.BaseRepositoryFactory
import org.gradle.api.internal.artifacts.dsl.DefaultRepositoryHandler

@SerialName("maven")
@Serializable
internal data class ResolvedMavenRepository(
    override val id: String,
    val url: String? = null,
) : ResolvedRepository {
    companion object {
        val Google = ResolvedMavenRepository(
            id = GOOGLE_REPOSITORY_ID,
            url = ArtifactRepositoryContainer.GOOGLE_URL.removeTrailingSlash(),
        )
        val GradlePluginPortal = ResolvedMavenRepository(
            id = GRADLE_PLUGIN_PORTAL_REPOSITORY_ID,
            url = System.getProperty(
                BaseRepositoryFactory.PLUGIN_PORTAL_OVERRIDE_URL_PROPERTY,
                BaseRepositoryFactory.PLUGIN_PORTAL_DEFAULT_URL,
            ).removeTrailingSlash(),
        )
        val JCenter = ResolvedMavenRepository(
            id = JCENTER_REPOSITORY_ID,
            url = DefaultRepositoryHandler.BINTRAY_JCENTER_URL.removeTrailingSlash(),
        )
        val MavenCentral = ResolvedMavenRepository(
            id = MAVEN_CENTRAL_REPOSITORY_ID,
            url = ArtifactRepositoryContainer.MAVEN_CENTRAL_URL.removeTrailingSlash(),
        )
        val MavenLocal = ResolvedMavenRepository(id = MAVEN_LOCAL_REPOSITORY_ID)
    }
}

internal const val GOOGLE_REPOSITORY_ID = "google"
internal const val GRADLE_PLUGIN_PORTAL_REPOSITORY_ID = "gradlePluginPortal"
internal const val JCENTER_REPOSITORY_ID = "jcenter"
internal const val MAVEN_CENTRAL_REPOSITORY_ID = "mavenCentral"
internal const val MAVEN_LOCAL_REPOSITORY_ID = "mavenLocal"

internal val DefaultMavenRepositoryMap: Map<String, ResolvedMavenRepository> =
    mapOf(
        DefaultRepositoryHandler.DEFAULT_BINTRAY_JCENTER_REPO_NAME to ResolvedMavenRepository.JCenter,
        DefaultRepositoryHandler.DEFAULT_MAVEN_CENTRAL_REPO_NAME to ResolvedMavenRepository.MavenCentral,
        DefaultRepositoryHandler.DEFAULT_MAVEN_LOCAL_REPO_NAME to ResolvedMavenRepository.MavenLocal,
        DefaultRepositoryHandler.GOOGLE_REPO_NAME to ResolvedMavenRepository.Google,
        DefaultRepositoryHandler.GRADLE_PLUGIN_PORTAL_REPO_NAME to ResolvedMavenRepository.GradlePluginPortal,
    )
