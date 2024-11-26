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
package me.omico.dehell.gradle.internal

import me.omico.dehell.DehellIgnoredRule
import me.omico.dehell.DehellMatchedRule
import me.omico.dehell.DehellRule
import me.omico.dehell.gradle.DehellRulesExtension

internal abstract class DehellRulesExtensionImpl : DehellRulesExtension {
    override fun match(name: String, url: String, by: DehellRule.By, type: DehellRule.Type, value: String) {
        DehellMatchedRule(
            name = name,
            url = url,
            by = by,
            type = type,
            value = value,
        ).also(matchedRules::add)
    }

    override fun match(
        name: String,
        url: String,
        by: DehellRule.By,
        type: DehellRule.Type,
        vararg values: String,
    ): Unit =
        values.forEach { value -> match(name, url, by, type, value) }

    override fun ignore(by: DehellRule.By, type: DehellRule.Type, value: String) {
        DehellIgnoredRule(
            by = by,
            type = type,
            value = value,
        ).also(ignoredRules::add)
    }

    override fun ignore(by: DehellRule.By, type: DehellRule.Type, vararg values: String): Unit =
        values.forEach { value -> ignore(by, type, value) }
}
