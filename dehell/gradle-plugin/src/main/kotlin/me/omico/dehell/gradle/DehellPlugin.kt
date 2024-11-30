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

import me.omico.dehell.gradle.internal.DehellRulesExtensionImpl
import me.omico.dehell.gradle.internal.services.registerDehellDependencyServiceIfAbsent
import me.omico.dehell.gradle.internal.tasks.DehellDependencyCollector
import me.omico.dehell.gradle.internal.tasks.createDehellCollectDependenciesTask
import me.omico.dehell.gradle.internal.tasks.createDehellDependencyAggregatorTask
import me.omico.dehell.gradle.internal.tasks.createDehellDependencyInfoGeneratorTask
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create

@Suppress("unused")
public class DehellPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = target.run {
        val dehellExtension = extensions.create<DehellExtension>(DehellExtension.NAME)
        val dehellRulesExtension = dehellExtension.extensions.create(
            publicType = DehellRulesExtension::class,
            name = DehellRulesExtension.NAME,
            instanceType = DehellRulesExtensionImpl::class,
        )
        val dehellDependencyServiceProvider = gradle.registerDehellDependencyServiceIfAbsent()
        val rootDehellCollectDependenciesTask = rootProject.tasks.maybeCreate(DehellDependencyCollector.NAME)
        afterEvaluate {
            val dehellCollectDependenciesTask = createDehellCollectDependenciesTask(
                dehellExtension = dehellExtension,
                dependencyServiceProvider = dehellDependencyServiceProvider,
            )
            val dehellDependencyAggregatorTask = createDehellDependencyAggregatorTask(
                dehellExtension = dehellExtension,
                dependencyServiceProvider = dehellDependencyServiceProvider,
            )
            val dehellDependencyInfoGeneratorTask = createDehellDependencyInfoGeneratorTask(
                dehellExtension = dehellExtension,
                dehellRulesExtension = dehellRulesExtension,
            )
            rootDehellCollectDependenciesTask.dependsOn(dehellCollectDependenciesTask)
            dehellDependencyAggregatorTask.dependsOn(rootDehellCollectDependenciesTask)
            dehellDependencyInfoGeneratorTask.dependsOn(dehellDependencyAggregatorTask)
        }
    }
}
