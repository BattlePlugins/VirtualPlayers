plugins {
    id("java")
    id("java-library")
    id("com.github.johnrengelman.shadow") version "8.1.1"
}

group = "org.battleplugins"
version = "3.0.0"

java {
    toolchain.languageVersion.set(JavaLanguageVersion.of(21))
}

allprojects {
    repositories {
        maven("https://repo.papermc.io/repository/maven-public/")
    }

    apply {
        plugin("java")
        plugin("java-library")
        plugin("com.github.johnrengelman.shadow")
    }
}