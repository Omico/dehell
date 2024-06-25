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

import me.omico.dehell.gradle.DehellExtension
import org.gradle.api.Project
import java.io.File
import javax.inject.Inject

internal abstract class DehellExtensionImpl(
    @Inject private val project: Project,
) : DehellExtension {
    override var variant: String? = null
    override var output: File = project.file("dehell-dependencies.json")
    override var debug: Boolean = false
}
