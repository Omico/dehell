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
package me.omico.dehell.serialization

import kotlinx.serialization.Serializable
import me.omico.dehell.DehellMatchedRule

@Serializable
public data class DehellDependencyInfo(
    val dependencies: Set<Dependency> = emptySet(),
    val mismatchedDependencies: Set<String> = emptySet(),
) {
    @Serializable
    public data class Dependency(
        val name: String,
        val url: String,
    ) : Comparable<Dependency> {
        override fun compareTo(other: Dependency): Int = name.compareTo(other.name)
    }
}

public fun DehellMatchedRule.toDependency(): DehellDependencyInfo.Dependency =
    DehellDependencyInfo.Dependency(
        name = name,
        url = url,
    )
