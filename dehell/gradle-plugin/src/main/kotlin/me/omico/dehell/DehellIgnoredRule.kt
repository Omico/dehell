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

public data class DehellIgnoredRule(
    override val by: By,
    override val type: Type,
    override val value: String,
) : DehellRule() {
    override fun compareTo(other: DehellRule): Int {
        if (other !is DehellIgnoredRule) return 1
        return compareValuesBy(
            this, other,
            DehellIgnoredRule::by,
            DehellIgnoredRule::type,
            DehellIgnoredRule::value,
        )
    }
}
