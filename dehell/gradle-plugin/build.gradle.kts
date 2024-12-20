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

dependencies {
    testImplementation(gradleTestKit())
    testImplementation(junit.jupiter)
    testRuntimeOnly(junit.platform.launcher)
}

tasks.test {
    dependsOn(tasks.publishToMavenLocal)
    useJUnitPlatform()
    environment("DEHELL_VERSION", version)
}
