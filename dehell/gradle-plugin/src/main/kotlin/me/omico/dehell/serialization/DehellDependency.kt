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

@Serializable
public sealed interface DehellDependency

public object DehellDependencyComparator : Comparator<DehellDependency> {
    override fun compare(o1: DehellDependency, o2: DehellDependency): Int =
        when {
            o1 is DehellProjectDependency && o2 is DehellProjectDependency -> o1.compareTo(o2)
            o1 is DehellModuleDependency && o2 is DehellModuleDependency -> o1.compareTo(o2)
            else -> o1::class.qualifiedName!!.compareTo(o2::class.qualifiedName!!)
        }
}
