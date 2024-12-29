plugins {
    id("java")
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1" apply false
}

allprojects {
    apply {
        plugin("java")
        plugin("java-library")
    }

    group = "org.battleplugins.virtualplayers"
    version = "3.0.1"

    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    java {
        withJavadocJar()
        withSourcesJar()
        toolchain {
            languageVersion.set(JavaLanguageVersion.of(21))
        }
    }
}
