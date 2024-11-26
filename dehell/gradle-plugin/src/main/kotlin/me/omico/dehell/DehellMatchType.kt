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
package me.omico.dehell

import java.io.Serializable

public sealed class DehellMatchType(
    private val weight: Int,
) : Serializable,
    Comparable<DehellMatchType> {
    public fun readResolve(): Any = this

    override fun compareTo(other: DehellMatchType): Int = weight.compareTo(other.weight)
    override fun toString(): String = javaClass.simpleName

    public object Exact : DehellMatchType(0)
    public object Prefix : DehellMatchType(1)
    public object Regex : DehellMatchType(2)

    public companion object {
        @Suppress("unused")
        private const val serialVersionUID: Long = 1L
    }
}
