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

import me.omico.dehell.DehellIgnoredRule
import me.omico.dehell.DehellMatchedRule
import me.omico.dehell.DehellRule
import me.omico.dehell.gradle.DehellExtension
import me.omico.dehell.gradle.DehellRulesExtension
import me.omico.dehell.serialization.DehellDependencyInfo
import me.omico.dehell.serialization.DehellModuleDependency
import me.omico.dehell.serialization.internal.readJson
import me.omico.dehell.serialization.internal.writeJsonToFile
import me.omico.dehell.serialization.toDependency
import org.gradle.api.Project
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.provider.SetProperty
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.InputFile
import org.gradle.api.tasks.OutputFile
import org.gradle.api.tasks.TaskAction
import org.gradle.kotlin.dsl.create

internal abstract class DehellDependencyInfoGenerator : DehellTask() {
    @get:InputFile
    abstract val aggregatedDependenciesFileProperty: RegularFileProperty

    @get:Input
    abstract val matchedRuleSetProperty: SetProperty<DehellMatchedRule>

    @get:Input
    abstract val ignoredRuleSetProperty: SetProperty<DehellIgnoredRule>

    @get:OutputFile
    abstract val dependencyInfoOutputFileProperty: RegularFileProperty

    fun Project.configureDehellDependencyInfoGenerator(
        dehellExtension: DehellExtension,
        dehellRulesExtension: DehellRulesExtension,
    ) {
        outputs.upToDateWhen { false }
        aggregatedDependenciesFileProperty.convention(dehellExtension.dependencyAggregatorOutputFile)
        matchedRuleSetProperty.convention(dehellRulesExtension.matchedRules)
        ignoredRuleSetProperty.convention(dehellRulesExtension.ignoredRules)
        dependencyInfoOutputFileProperty.convention(dehellExtension.dependencyInfoGeneratorOutputFile)
    }

    @TaskAction
    protected fun generate() {
        val aggregatedDependenciesFile = aggregatedDependenciesFileProperty.get().asFile
        val matchedRuleSet = matchedRuleSetProperty.get()
        val ignoredRuleSet = ignoredRuleSetProperty.get()
        val dependencyInfoFile = dependencyInfoOutputFileProperty.get().asFile
        val dependencies = mutableSetOf<DehellModuleDependency>()
            .apply { addAll(aggregatedDependenciesFile.readJson()) }
        val matchedRules = mutableSetOf<DehellMatchedRule>()
            .apply { addAll(matchedRuleSet) }
            .toSortedSet()
        val ignoredRules = mutableSetOf<DehellIgnoredRule>()
            .apply { addAll(ignoredRuleSet) }
            .toSortedSet()
        val ignoredDependencies = mutableSetOf<DehellModuleDependency>()
        val matchedDependencies = mutableSetOf<DehellModuleDependency>()
        val resultDependencies = mutableSetOf<DehellDependencyInfo.Dependency>()
        ignoredRules.forEach { rule ->
            matchEachDependency(dependencies, rule) { dependency ->
                ignoredDependencies.add(dependency)
            }
        }
        dependencies.removeAll(ignoredDependencies)
        matchedRules.forEach { rule ->
            matchEachDependency(dependencies, rule) { dependency ->
                matchedDependencies.add(dependency)
                resultDependencies.add(rule.toDependency())
            }
        }
        val dependencyInfo = DehellDependencyInfo(
            dependencies = resultDependencies.sorted(),
            mismatchedDependencies = run {
                dependencies
                    .subtract(matchedDependencies)
                    .map(DehellModuleDependency::module)
                    .sorted()
            },
        )
        dependencyInfo.writeJsonToFile(dependencyInfoFile)
    }

    private fun <T : DehellRule> matchEachDependency(
        dependencies: Set<DehellModuleDependency>,
        rule: T,
        block: (dependency: DehellModuleDependency) -> Unit,
    ): Unit =
        dependencies.forEach { dependency ->
            val targetValue = when (rule.by) {
                DehellRule.By.Group -> dependency.group
                DehellRule.By.Name -> dependency.name
                DehellRule.By.Module -> dependency.module
            }
            val result = when (rule.type) {
                DehellRule.Type.Exact -> rule.value == targetValue
                DehellRule.Type.Prefix -> targetValue.startsWith(rule.value)
                DehellRule.Type.Regex -> rule.value.toRegex().matches(targetValue)
            }
            if (result) block(dependency)
        }

    companion object {
        const val NAME: String = "dehellDependencyInfo"
    }
}

internal fun Project.createDehellDependencyInfoGeneratorTask(
    dehellExtension: DehellExtension,
    dehellRulesExtension: DehellRulesExtension,
): DehellDependencyInfoGenerator =
    tasks.create<DehellDependencyInfoGenerator>(DehellDependencyInfoGenerator.NAME) {
        configureDehellDependencyInfoGenerator(dehellExtension, dehellRulesExtension)
    }
