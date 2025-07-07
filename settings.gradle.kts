pluginManagement {
    val kotlinVersion: String by settings
    val foojayVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        id("org.gradle.toolchains.foojay-resolver-convention") version foojayVersion
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention")
}

rootProject.name = "jpa-lean-projection"