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
import org.gradle.api.artifacts.component.ModuleComponentSelector

@Serializable
@SerialName("module")
public data class DehellModuleDependency(
    val group: String,
    val name: String,
    val version: String,
) : DehellDependency,
    Comparable<DehellModuleDependency> {
    val module: String = "$group:$name"

    override fun compareTo(other: DehellModuleDependency): Int =
        compareValuesBy(
            a = this, b = other,
            DehellModuleDependency::group,
            DehellModuleDependency::name,
            DehellModuleDependency::version,
        )
}

public fun ModuleComponentSelector.toDehellModuleDependency(): DehellModuleDependency =
    DehellModuleDependency(
        group = group,
        name = module,
        version = version,
    )
