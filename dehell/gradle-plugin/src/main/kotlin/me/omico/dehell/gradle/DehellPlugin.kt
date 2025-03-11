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
package me.omico.dehell.gradle

import me.omico.dehell.gradle.internal.DehellRulesExtensionImpl
import me.omico.dehell.gradle.internal.services.registerDehellDependencyServiceIfAbsent
import me.omico.dehell.gradle.internal.tasks.DehellDependencyCollector
import me.omico.dehell.gradle.internal.tasks.registerDehellCollectDependenciesTask
import me.omico.dehell.gradle.internal.tasks.registerDehellDependencyAggregatorTask
import me.omico.dehell.gradle.internal.tasks.registerDehellDependencyInfoGeneratorTask
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
        val dehellCollectDependenciesTask = registerDehellCollectDependenciesTask(
            dehellExtension = dehellExtension,
            dependencyServiceProvider = dehellDependencyServiceProvider,
        )
        val dehellDependencyAggregatorTask = registerDehellDependencyAggregatorTask(
            dehellExtension = dehellExtension,
            dependencyServiceProvider = dehellDependencyServiceProvider,
        )
        val dehellDependencyInfoGeneratorTask = registerDehellDependencyInfoGeneratorTask(
            dehellExtension = dehellExtension,
            dehellRulesExtension = dehellRulesExtension,
        )
        if (rootProject == this) {
            dehellDependencyAggregatorTask.configure {
                dependsOn(dehellCollectDependenciesTask)
            }
        } else {
            val rootDehellCollectDependenciesTask = rootProject.tasks.maybeCreate(DehellDependencyCollector.NAME)
            rootDehellCollectDependenciesTask.dependsOn(dehellCollectDependenciesTask)
            dehellDependencyAggregatorTask.configure {
                dependsOn(rootDehellCollectDependenciesTask)
            }
        }
        dehellDependencyInfoGeneratorTask.configure {
            dependsOn(dehellDependencyAggregatorTask)
        }
    }
}
