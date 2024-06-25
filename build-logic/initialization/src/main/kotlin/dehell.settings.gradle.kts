import me.omico.gradle.initialization.includeAllSubprojectModules
import me.omico.gradm.addDeclaredRepositories

addDeclaredRepositories()

plugins {
    id("dehell.develocity")
    id("dehell.gradm")
}

includeBuild("build-logic/project")

includeAllSubprojectModules("dehell")
