@file:Suppress("UnstableApiUsage")

plugins {
    `kotlin-dsl`
    embeddedKotlin("plugin.serialization")
    id("dehell.publishing")
}

kotlin {
    explicitApi()
}

gradlePlugin {
    plugins {
        create("dehell") {
            id = "me.omico.dehell"
            implementationClass = "me.omico.dehell.gradle.DehellPlugin"
        }
    }
}

dependencies {
    implementation(kotlinx.serialization.json)
}
