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

import kotlinx.serialization.Serializable
import org.gradle.api.artifacts.component.ComponentSelector
import org.gradle.api.artifacts.component.ModuleComponentSelector
import org.gradle.api.artifacts.component.ProjectComponentSelector

@Serializable
public sealed interface DehellDependency

public typealias DehellDependencyList = List<DehellDependency>

public fun ComponentSelector.toDehellDependency(): DehellDependency =
    when (this) {
        is ProjectComponentSelector -> toDehellProjectDependency()
        is ModuleComponentSelector -> toDehellModuleDependency()
        else -> error("Unsupported component selector: $this")
    }

public object DehellDependencyComparator : Comparator<DehellDependency> {
    override fun compare(a: DehellDependency, b: DehellDependency): Int =
        when {
            a is DehellProjectDependency && b is DehellProjectDependency -> a.compareTo(b)
            a is DehellModuleDependency && b is DehellModuleDependency -> a.compareTo(b)
            else -> a::class.qualifiedName!!.compareTo(b::class.qualifiedName!!)
        }
}