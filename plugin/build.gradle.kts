plugins {
    id("io.papermc.paperweight.userdev") version "1.7.1"
    id("xyz.jpenilla.run-paper") version "2.3.0"
}

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