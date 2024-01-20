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
import me.omico.dehell.gradle.internal.DehellExtensionImpl
import org.gradle.api.Plugin
import org.gradle.api.Project
import org.gradle.kotlin.dsl.create
import org.gradle.kotlin.dsl.get
import org.gradle.kotlin.dsl.register
import org.gradle.kotlin.dsl.registerIfAbsent

@Suppress("unused")
class DehellPlugin : Plugin<Project> {
    override fun apply(target: Project): Unit = target.run {
        val dehellExtension = extensions.create(
            publicType = DehellExtension::class,
            name = "dehell",
            instanceType = DehellExtensionImpl::class,
        )
        val dehellRulesExtension = dehellExtension.extensions.create(
            publicType = DehellRulesExtension::class,
            name = "rules",
            instanceType = DehellRulesExtensionImpl::class,
        )
        afterEvaluate {
            val dehellDependenciesServiceProvider = gradle.sharedServices.registerIfAbsent(
                name = "dehellDependenciesService",
                implementationType = DehellDependenciesService::class,
                configureAction = {
                    parameters.run {
                        taskNames.set(gradle.startParameter.taskNames)
                        output.set(dehellExtension.output)
                        rules.set(dehellRulesExtension.rules)
                        ignoreRules.set(dehellRulesExtension.ignoreRules)
                        debug.set(dehellExtension.debug)
                        dehellDirectory.set(layout.buildDirectory.dir("dehell"))
                    }
                },
            )
            tasks.register<DehellDependenciesTask>(DehellDependenciesTask.NAME) {
                dependsOn("dependencies")
                usesService(dehellDependenciesServiceProvider)
            }
            val configuration = configurations[dehellExtension.configuration]
            val dehellDependenciesService = dehellDependenciesServiceProvider.get()
            configuration.resolutionStrategy {
                eachDependency(dehellDependenciesService::add)
            }
        }
    }
}
