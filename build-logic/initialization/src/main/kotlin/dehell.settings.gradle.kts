import me.omico.gradle.initialization.includeAllSubprojectModules
import me.omico.gradm.addDeclaredRepositories

addDeclaredRepositories()

plugins {
    id("dehell.gradm")
    id("dehell.gradle-enterprise")
}

includeBuild("build-logic/project")

includeAllSubprojectModules("dehell")
