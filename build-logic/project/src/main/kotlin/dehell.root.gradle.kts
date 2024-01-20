import me.omico.consensus.dsl.requireRootProject

plugins {
    id("dehell.gradm")
    id("dehell.root.git")
    id("dehell.root.spotless")
}

requireRootProject()

consensus {
    allprojects {
        group = gradleProperty("project.group")
        version = gradleProperty("project.version")
    }
}

val wrapper: Wrapper by tasks.named<Wrapper>("wrapper") {
    gradleVersion = versions.gradle
    distributionType = Wrapper.DistributionType.BIN
}
