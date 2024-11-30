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

import me.omico.dehell.gradle.internal.services.DehellDependencyService
import org.gradle.api.Project
import org.gradle.api.provider.Property
import org.gradle.api.provider.Provider
import org.gradle.api.tasks.Input
import org.gradle.api.tasks.Internal

internal abstract class DehellDependencyTask : DehellTask() {
    @get:Input
    abstract val projectPathProperty: Property<String>

    @get:Internal
    abstract val dependencyServiceProperty: Property<DehellDependencyService>

    protected fun Project.configureDehellDependencyTask(dependencyServiceProvider: Provider<DehellDependencyService>) {
        // Always run the task to ensure that the DehellDependencyService can grab the latest dependencies.
        outputs.upToDateWhen { false }
        usesService(dependencyServiceProvider)
        dependencyServiceProperty.convention(dependencyServiceProvider)
        projectPathProperty.convention(path)
    }
}
