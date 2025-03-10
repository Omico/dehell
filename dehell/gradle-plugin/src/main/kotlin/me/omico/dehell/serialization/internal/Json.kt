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
package me.omico.dehell.serialization.internal

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import me.omico.dehell.internal.ensureEndsWithNewLine
import java.io.File

@OptIn(ExperimentalSerializationApi::class)
private val json: Json =
    Json {
        prettyPrint = true
        prettyPrintIndent = "  "
        ignoreUnknownKeys = true
    }

internal inline fun <reified T> T.toJson(): String = json.encodeToString(this)

internal inline fun <reified T> String.fromJson(): T = json.decodeFromString(this)

internal inline fun <reified T> T.writeJsonToFile(file: File): Unit = file.writeText(toJson().ensureEndsWithNewLine())

internal inline fun <reified T> File.readJson(): T = readText().fromJson()
