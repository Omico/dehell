# Dehell

> What the hell? Incoming dependencies. Such a dependency hell.

## Usage

```kotlin
repositories {
    maven(url = "https://maven.omico.me")
}

```

### Android

```kotlin
plugins {
    id("me.omico.dehell") version "<version>"
}

dehell {
    variant = "release"
    dependencyCollectorOutputFile = file("build/dehell/dependencies.json") // Optional
    dependencyAggregatorOutputFile = file("build/dehell/dependencies-aggregated.json") // Optional
    dependencyInfoGeneratorOutputFile = file("dehell-dependencies.json") // Optional
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
