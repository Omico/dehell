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
    id("me.omico.dehell") version "0.2.0"
}

dehell {
    variant = "release"
    debug = true
    output = file("src/main/res/raw/dehell-dependencies.json")
    rules {
        match(
            name = "Jetpack",
            url = "https://developer.android.com/jetpack/androidx/explorer",
            by = DehellMatchBy.Group,
            type = DehellMatchType.Prefix,
            value = "androidx.",
        )
        match(
            name = "Jetpack Compose",
            url = "https://developer.android.com/jetpack/androidx/explorer",
            by = DehellMatchBy.Group,
            type = DehellMatchType.Prefix,
            value = "androidx.compose.",
        )
        ignore(
            by = DehellMatchBy.Group,
            type = DehellMatchType.Exact,
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
