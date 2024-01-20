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

import me.omico.dehell.DehellIgnoredRule
import me.omico.dehell.DehellMatchedRule
import me.omico.dehell.DehellRule

interface DehellRulesExtension {
    val rules: Set<DehellMatchedRule>
    val ignoreRules: Set<DehellIgnoredRule>

    fun match(
        name: String,
        url: String,
        by: DehellRule.By,
        type: DehellRule.Type,
        value: String,
    )

    fun match(
        name: String,
        url: String,
        by: DehellRule.By,
        type: DehellRule.Type,
        vararg values: String,
    )

    fun ignore(
        by: DehellRule.By,
        type: DehellRule.Type,
        value: String,
    )

    fun ignore(
        by: DehellRule.By,
        type: DehellRule.Type,
        vararg values: String,
    )
}

abstract class DehellRulesExtensionImpl : DehellRulesExtension {
    override val rules: MutableSet<DehellMatchedRule> = mutableSetOf()
    override val ignoreRules: MutableSet<DehellIgnoredRule> = mutableSetOf()

    override fun match(name: String, url: String, by: DehellRule.By, type: DehellRule.Type, value: String) {
        DehellMatchedRule(
            name = name,
            url = url,
            matchBy = by,
            matchType = type,
            value = value,
        ).also(rules::add)
    }

    override fun match(
        name: String,
        url: String,
        by: DehellRule.By,
        type: DehellRule.Type,
        vararg values: String,
    ) {
        values.forEach { value ->
            match(name, url, by, type, value)
        }
    }

    override fun ignore(by: DehellRule.By, type: DehellRule.Type, value: String) {
        DehellIgnoredRule(
            matchBy = by,
            matchType = type,
            value = value,
        ).also(ignoreRules::add)
    }

    override fun ignore(by: DehellRule.By, type: DehellRule.Type, vararg values: String) {
        values.forEach { value ->
            ignore(by, type, value)
        }
    }
}
