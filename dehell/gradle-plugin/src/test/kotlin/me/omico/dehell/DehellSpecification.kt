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
package me.omico.dehell

import org.gradle.testkit.runner.BuildResult
import org.gradle.testkit.runner.GradleRunner
import org.junit.jupiter.api.BeforeEach
import org.junit.jupiter.api.io.TempDir
import java.io.File

abstract class DehellSpecification {
    @TempDir lateinit var testProjectDirectory: File
    lateinit var settingsKotlinScript: File
    lateinit var buildKotlinScript: File

    protected val dehellVersion: String = System.getenv("DEHELL_VERSION")

    @BeforeEach
    protected fun setup() {
        settingsKotlinScript = testProjectDirectory.resolve(GRADLE_KOTLIN_SETTINGS_SCRIPT_NAME)
        buildKotlinScript = testProjectDirectory.resolve(GRADLE_KOTLIN_BUILD_SCRIPT_NAME)
    }

    fun runTest(
        settingsKotlinScriptContent: () -> String = { "" },
        buildKotlinScriptContent: () -> String = { "" },
        vararg arguments: String = emptyArray(),
        submodules: SubmoduleCreator.() -> Unit = {},
        result: BuildResult.() -> Unit,
    ) {
        settingsKotlinScript.writeText(settingsKotlinScriptContent())
        buildKotlinScript.writeText(buildKotlinScriptContent())
        SubmoduleCreatorImpl(testProjectDirectory).submodules()
        val buildResult = GradleRunner.create()
            .withProjectDir(testProjectDirectory)
            .withArguments(*arguments)
            .forwardOutput()
            .build()
        result(buildResult)
    }

    interface SubmoduleCreator {
        fun submodule(
            name: String,
            buildKotlinScriptContent: () -> String = { "" },
        )
    }

    private class SubmoduleCreatorImpl(
        private val testProjectDirectory: File,
    ) : SubmoduleCreator {
        override fun submodule(
            name: String,
            buildKotlinScriptContent: () -> String,
        ) {
            val submoduleDirectory = testProjectDirectory.resolve(name)
            submoduleDirectory.mkdirs()
            val buildKotlinScript = submoduleDirectory.resolve(GRADLE_KOTLIN_BUILD_SCRIPT_NAME)
            buildKotlinScript.writeText(buildKotlinScriptContent())
        }
    }
}

private const val GRADLE_KOTLIN_BUILD_SCRIPT_NAME: String = "build.gradle.kts"
private const val GRADLE_KOTLIN_SETTINGS_SCRIPT_NAME: String = "settings.gradle.kts"
