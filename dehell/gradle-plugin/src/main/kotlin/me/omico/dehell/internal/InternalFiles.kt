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
package me.omico.dehell.internal

import org.gradle.api.Project
import org.gradle.api.file.Directory
import org.gradle.api.file.RegularFile
import org.gradle.api.provider.Provider

internal val Project.dehellBuildDirectory: Provider<Directory>
    get() = layout.buildDirectory.dir("dehell")

internal val Project.defaultDehellDependencyCollectorOutputFile: Provider<RegularFile>
    get() = dehellBuildDirectory.map { directory -> directory.file("dependencies.json") }

internal val Project.defaultDehellDependencyAggregatorOutputFile: Provider<RegularFile>
    get() = dehellBuildDirectory.map { directory -> directory.file("dependencies-aggregated.json") }
