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

import me.omico.dehell.gradle.internal.defaultDehellDependencyAggregatorOutputFileProvider
import me.omico.dehell.gradle.internal.defaultDehellDependencyCollectorOutputFileProvider
import me.omico.dehell.gradle.internal.defaultDehellDependencyInfoGeneratorOutputFile
import org.gradle.api.file.ProjectLayout
import org.gradle.api.file.RegularFileProperty
import org.gradle.api.model.ObjectFactory
import org.gradle.api.plugins.ExtensionAware
import org.gradle.api.provider.Property
import javax.inject.Inject

public abstract class DehellExtension @Inject constructor(
    objects: ObjectFactory,
    layout: ProjectLayout,
) : ExtensionAware {
    public abstract val variant: Property<String>
    public val dependencyCollectorOutputFile: RegularFileProperty =
        objects.fileProperty().convention(layout.defaultDehellDependencyCollectorOutputFileProvider)
    public val dependencyAggregatorOutputFile: RegularFileProperty =
        objects.fileProperty().convention(layout.defaultDehellDependencyAggregatorOutputFileProvider)
    public val dependencyInfoGeneratorOutputFile: RegularFileProperty =
        objects.fileProperty().convention(layout.defaultDehellDependencyInfoGeneratorOutputFile)

    public companion object {
        public const val NAME: String = "dehell"
    }
}
