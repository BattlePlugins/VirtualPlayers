plugins {
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("xyz.jpenilla.run-paper") version "2.3.0"
    id("com.modrinth.minotaur") version "2.+"
}

val supportedVersions = listOf("1.20.6", "1.21")

repositories {
    maven("https://repo.papermc.io/repository/maven-public/")
}

dependencies {
    api(project(":api"))

    paperweight.paperDevBundle("1.20.6-R0.1-SNAPSHOT")
}

tasks {
    runServer {
        minecraftVersion("1.20.6")
    }

    shadowJar {
        from("src/main/java/resources") {
            include("*")
        }

        archiveFileName.set("VirtualPlayers.jar")
        archiveClassifier.set("")
    }

    jar {
        archiveClassifier.set("unshaded")
    }

    processResources {
        expand("version" to rootProject.version)
    }

    named("build") {
        dependsOn(shadowJar)
    }
}

modrinth {
    token.set(System.getenv("MODRINTH_TOKEN") ?: "")
    projectId.set("virtualplayers")
    versionNumber.set(rootProject.version as String + "-" + System.getenv("BUILD_NUMBER"))
    versionType.set(if ("SNAPSHOT" in rootProject.version.toString()) "beta" else "release")
    changelog.set(System.getenv("CHANGELOG") ?: "")
    uploadFile.set(tasks.shadowJar)
    gameVersions.set(supportedVersions)
}