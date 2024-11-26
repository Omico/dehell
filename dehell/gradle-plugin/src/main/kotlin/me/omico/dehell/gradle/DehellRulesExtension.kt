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
import me.omico.dehell.DehellRule.By
import me.omico.dehell.DehellRule.Type
import org.gradle.api.provider.SetProperty

public interface DehellRulesExtension {
    public val matchedRules: SetProperty<DehellMatchedRule>
    public val ignoredRules: SetProperty<DehellIgnoredRule>
    public fun match(name: String, url: String, by: By, type: Type, value: String)
    public fun match(name: String, url: String, by: By, type: Type, vararg values: String)
    public fun ignore(by: By, type: Type, value: String)
    public fun ignore(by: By, type: Type, vararg values: String)

    public val group: By get() = By.Group
    public val name: By get() = By.Name
    public val module: By get() = By.Module

    public val prefix: Type get() = Type.Prefix
    public val exact: Type get() = Type.Exact
    public val regex: Type get() = Type.Regex

    public companion object {
        public const val NAME: String = "rules"
    }
}
