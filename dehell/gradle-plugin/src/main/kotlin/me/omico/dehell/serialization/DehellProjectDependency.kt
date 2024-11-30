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
package me.omico.dehell.serialization

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import org.gradle.api.artifacts.component.ProjectComponentSelector

@Serializable
@SerialName("project")
public data class DehellProjectDependency(
    val path: String,
) : DehellDependency,
    Comparable<DehellProjectDependency> {
    override fun compareTo(other: DehellProjectDependency): Int =
        compareValuesBy(
            a = this, b = other,
            DehellProjectDependency::path,
        )
}

public typealias DehellProjectDependencyList = List<DehellProjectDependency>

public fun ProjectComponentSelector.toDehellProjectDependency(): DehellProjectDependency =
    DehellProjectDependency(path = projectPath)
