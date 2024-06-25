plugins {
    id("maven-publish")
}

dependencies {
    compileOnlyApi(libs.paper.api)
}

publishing {
    val isSnapshot = "SNAPSHOT" in version.toString()

    repositories {
        maven {
            name = "battleplugins"
            url = uri("https://repo.battleplugins.org/${if (isSnapshot) "snapshots" else "releases"}")
            credentials(PasswordCredentials::class)
            authentication {
                create<BasicAuthentication>("basic")
            }
        }

        publications {
            create<MavenPublication>("mavenJava") {
                from(components["java"])
                pom {
                    packaging = "jar"
                    url.set("https://github.com/BattlePlugins/VirtualPlayers")

                    scm {
                        connection.set("scm:git:git://github.com/BattlePlugins/VirtualPlayers.git")
                        developerConnection.set("scm:git:ssh://github.com/BattlePlugins/VirtualPlayers.git")
                        url.set("https://github.com/BattlePlugins/VirtualPlayers");
                    }

                    licenses {
                        license {
                            name.set("GNU General Public License v3.0")
                            url.set("https://www.gnu.org/licenses/gpl-3.0.html")
                        }
                    }

                    developers {
                        developer {
                            name.set("BattlePlugins Team")
                            organization.set("BattlePlugins")
                            organizationUrl.set("https://github.com/BattlePlugins")
                        }
                    }
                }
            }
        }
    }
}