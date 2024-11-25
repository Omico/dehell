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
package me.omico.dehell.gradle.internal

import kotlinx.serialization.encodeToString
import me.omico.dehell.DehellIgnoreRule
import me.omico.dehell.DehellMatchBy
import me.omico.dehell.DehellMatchRule
import me.omico.dehell.DehellMatchType
import me.omico.dehell.DehellRule
import me.omico.dehell.gradle.DehellDependenciesTask
import me.omico.dehell.internal.ensureEndsWithNewLine
import me.omico.dehell.serialization.DehellDependencyInfo
import me.omico.dehell.serialization.internal.prettyJson
import org.gradle.api.artifacts.component.ModuleComponentSelector
import org.gradle.api.file.DirectoryProperty
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.ListProperty
import org.gradle.api.provider.Property
import org.gradle.api.provider.SetProperty
import org.gradle.api.services.BuildService
import org.gradle.api.services.BuildServiceParameters

internal abstract class DehellDependenciesService :
    BuildService<DehellDependenciesService.Parameters>,
    AutoCloseable {
    private val dependencies = mutableSetOf<Dependency>()
    private val matchedDependencies = mutableSetOf<Dependency>()
    private val resultDependencies = mutableSetOf<DehellDependencyInfo.Dependency>()

    fun add(moduleComponentSelector: ModuleComponentSelector) {
        dependencies.add(
            Dependency(
                group = moduleComponentSelector.group,
                name = moduleComponentSelector.module,
            ),
        )
    }

    override fun close() {
        if (DehellDependenciesTask.NAME !in parameters.taskNames.get()) return
        val debug = parameters.debug.get()
        val dehellDirectory = parameters.dehellDirectory.get().asFile
        if (dehellDirectory.exists()) dehellDirectory.deleteRecursively()
        dehellDirectory.mkdirs()
        if (debug) {
            dehellDirectory.resolve("dependencies.txt")
                .writeText(dependencies.sorted().joinToString("\n", transform = Dependency::module))
        }
        val ignoreRules = parameters.ignoreRules.get()
        val ignoreDependencies = mutableSetOf<Dependency>()
        ignoreRules.sorted().forEach { rule ->
            rule.matchEachDependency { dependency ->
                ignoreDependencies.add(dependency)
            }
        }
        dependencies.removeAll(ignoreDependencies)
        val rules = parameters.rules.get()
        rules.sorted().forEach { rule ->
            rule.matchEachDependency { dependency ->
                matchedDependencies.add(dependency)
                resultDependencies.add(DehellDependencyInfo.Dependency(rule.name, rule.url))
            }
        }
        val dependencyInfo = DehellDependencyInfo(
            dependencies = resultDependencies.sorted(),
            mismatchedDependencies = dependencies.subtract(matchedDependencies).map(Dependency::module).sorted(),
        )
        val dependencyInfoContent = prettyJson.encodeToString(dependencyInfo)
        parameters.output.get().asFile.run {
            parentFile.mkdirs()
            writeText(dependencyInfoContent.ensureEndsWithNewLine())
        }
        if (debug) {
            dehellDirectory.resolve("dehell-dependencies.json").writeText(dependencyInfoContent)
            if (dependencyInfo.mismatchedDependencies.isNotEmpty()) {
                dehellDirectory.resolve("mismatched-dependencies.txt")
                    .writeText(dependencyInfo.mismatchedDependencies.joinToString("\n"))
            }
        }
    }

    private fun <T : DehellRule> T.matchEachDependency(block: (dependency: Dependency) -> Unit): Unit =
        dependencies.forEach { dependency ->
            val targetValue: String = when (matchBy) {
                is DehellMatchBy.Group -> dependency.group
                is DehellMatchBy.Name -> dependency.name
                is DehellMatchBy.Module -> dependency.module
            }
            val result = when (matchType) {
                is DehellMatchType.Exact -> value == targetValue
                is DehellMatchType.Prefix -> targetValue.startsWith(value)
                is DehellMatchType.Regex -> value.toRegex().matches(targetValue)
            }
            if (result) block(dependency)
        }

    interface Parameters : BuildServiceParameters {
        val taskNames: ListProperty<String>
        val output: RegularFileProperty
        val rules: SetProperty<DehellMatchRule>
        val ignoreRules: SetProperty<DehellIgnoreRule>
        val debug: Property<Boolean>
        val dehellDirectory: DirectoryProperty
    }

    companion object {
        const val NAME: String = "DehellDependenciesService"
    }
}
