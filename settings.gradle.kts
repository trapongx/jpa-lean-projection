pluginManagement {
    val kotlinVersion: String by settings
    val foojayVersion: String by settings
    val springBootVersion: String by settings
    val springMgmtVersion: String by settings

    plugins {
        kotlin("jvm") version kotlinVersion
        kotlin("plugin.jpa") version kotlinVersion
        kotlin("plugin.spring") version kotlinVersion
        id("org.gradle.toolchains.foojay-resolver-convention") version foojayVersion
        id("org.springframework.boot") version springBootVersion
        id("io.spring.dependency-management") version springMgmtVersion
    }
}

plugins {
    id("org.gradle.toolchains.foojay-resolver-convention")
}

rootProject.name = "jpa-lean-projection"