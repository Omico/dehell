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

abstract class DehellRule : Serializable, Comparable<DehellRule> {
    abstract val matchBy: By
    abstract val matchType: Type
    abstract val value: String

    override fun compareTo(other: DehellRule): Int {
        var result = matchType.compareTo(other.matchType)
        if (result != 0) return result
        result = matchBy.compareTo(other.matchBy)
        if (result != 0) return result
        // Compare by value
        val otherValue = other.value
        if (value == otherValue) return 0
        if (value.startsWith(otherValue)) return -1
        if (otherValue.startsWith(value)) return 1
        if (value.length > otherValue.length) return -1
        if (value.length < otherValue.length) return 1
        return 0
    }

    companion object {
        @Suppress("ConstPropertyName")
        private const val serialVersionUID = 1L
    }

    sealed class By(
        private val weight: Int,
    ) : Serializable, Comparable<By> {
        fun readResolve(): Any = this

        override fun compareTo(other: By): Int = weight.compareTo(other.weight)
        override fun toString(): String = javaClass.simpleName

        object Group : By(0)
        object Name : By(1)
        object Module : By(2)

        companion object {
            @Suppress("ConstPropertyName")
            private const val serialVersionUID = 1L
        }
    }

    sealed class Type(
        private val weight: Int,
    ) : Serializable, Comparable<Type> {
        fun readResolve(): Any = this

        override fun compareTo(other: Type): Int = weight.compareTo(other.weight)
        override fun toString(): String = javaClass.simpleName

        object Exact : Type(0)
        object Prefix : Type(1)
        object Regex : Type(2)

        companion object {
            @Suppress("ConstPropertyName")
            private const val serialVersionUID = 1L
        }
    }
}
