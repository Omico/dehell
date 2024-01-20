import me.omico.consensus.dsl.by
import me.omico.consensus.dsl.isAutomatedPublishingGradlePlugin

plugins {
    id("me.omico.consensus.publishing")
}

consensus {
    publishing {
        publishToLocalRepository("MAVEN_OMICO_LOCAL_URI")
        afterEvaluate {
            if (!isAutomatedPublishingGradlePlugin) {
                createMavenPublication {
                    from(components["java"])
                }
            }
            publications.all {
                if (this !is MavenPublication) return@all
                pom {
                    name by gradleProperty("POM_NAME")
                    description by gradleProperty("POM_DESCRIPTION")
                    url by "https://github.com/Omico/dehell"
                    licenses {
                        license {
                            name by "The Apache Software License, Version 2.0"
                            url by "https://www.apache.org/licenses/LICENSE-2.0.txt"
                        }
                    }
                    developers {
                        developer {
                            id by "Omico"
                            name by "Omico"
                        }
                    }
                    scm {
                        url by "https://github.com/Omico/dehell"
                        connection by "scm:git:https://github.com/Omico/dehell.git"
                        developerConnection by "scm:git:https://github.com/Omico/dehell.git"
                    }
                }
            }
            signing {
                if (isSnapshot) return@signing
                useGpgCmd()
                sign(publications)
            }
        }
    }
}

extensions.findByType<JavaPluginExtension>()?.apply {
    withSourcesJar()
    withJavadocJar()
}
