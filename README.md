# Dehell

> What the hell? Incoming dependencies. Such a dependency hell.

## Usage

In the root `settings.gradle.kts` file, add the following:

```kotlin
pluginManagement {
    repositories {
        maven(url = "https://maven.omico.me")
        gradlePluginPortal()
    }
}
```

In your main `build.gradle.kts` file (for example on Android is `app/build.gradle.kts`), add the following:

```kotlin
plugins {
    id("me.omico.dehell") version "<version>"
}

dehell {
    // On Android, use `release` or `debug` or any other variant.
    variant = "release"
    
    // The following three lines are optional, usually not necessary to change.
    dependencyCollectorOutputFile = file("build/dehell/dependencies.json") 
    dependencyAggregatorOutputFile = file("build/dehell/dependencies-aggregated.json")
    dependencyInfoGeneratorOutputFile = file("dehell-dependencies.json")
    
    // Only you use `dehellDependencyInfo` task, you should set the following rules to match or ignore dependencies.
    rules {
        match(
            name = "Jetpack",
            url = "https://developer.android.com/jetpack/androidx/explorer",
            by = group,
            type = prefix,
            value = "androidx.",
        )
        match(
            name = "Jetpack Compose",
            url = "https://developer.android.com/jetpack/androidx/explorer",
            by = group,
            type = prefix,
            value = "androidx.compose.",
        )
        ignore(
            by = group,
            type = exact,
            values = arrayOf(
                "com.example",
                "org.jetbrains",
            ),
        )
    }
}
```

## License

```txt
Copyright 2024 Omico

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

    https://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
```
