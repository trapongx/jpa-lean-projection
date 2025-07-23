import org.gradle.kotlin.dsl.withType
import org.jetbrains.kotlin.gradle.tasks.KotlinCompile
import kotlin.collections.plus

val javaSdkVersion: String by project
val javaPersistenceVersion: String by project
val junitJupiterVersion: String by project
val dtoBuddyVersion: String by project
val snapFacadeVersion: String by project
val mockitoKotlinVersion: String by project

plugins {
    java
    kotlin("jvm")
    kotlin("plugin.jpa")
    kotlin("plugin.spring")
    id("org.springframework.boot")
    id("io.spring.dependency-management")
    `maven-publish`
}

group = "com.runninglane"
version = "1.7.0-SNAPSHOT"

repositories {
    mavenCentral()
    mavenLocal()
}

dependencies {
    // Java persistence
    implementation("javax.persistence:javax.persistence-api:$javaPersistenceVersion")

    // Kotlin Reflection
    implementation(kotlin("reflect"))

    // Runtime DTO class generation
    api("com.runninglane:dto-buddy:${dtoBuddyVersion}")

    // Runtime facade class generation
    api("com.runninglane:snap-facade:${snapFacadeVersion}")

    // Kotlin test assertions
    testImplementation(kotlin("test"))
    testImplementation(kotlin("test-junit5"))
    testImplementation("org.junit.jupiter:junit-jupiter-api:${junitJupiterVersion}")
    testRuntimeOnly("org.junit.jupiter:junit-jupiter-engine:${junitJupiterVersion}")

    // Mocking
    testImplementation("org.mockito.kotlin:mockito-kotlin:$mockitoKotlinVersion")

    // Persistence context for testing
    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.springframework.boot:spring-boot-starter-data-jpa")
    testImplementation("com.h2database:h2")
}

tasks.test {
    useJUnitPlatform()
}

kotlin {
    jvmToolchain {
        languageVersion.set(JavaLanguageVersion.of(javaSdkVersion.toInt()))
    }

    // Configure JSR-305 strict mode for proper nullability handling
    tasks.withType<KotlinCompile>().configureEach {
        kotlinOptions {
            // Enable JSR-305 strict mode for proper nullability with Java interop
            freeCompilerArgs += "-Xjsr305=strict"
        }
    }
}

// Configure Java plugin first to properly enable withSourcesJar
java {
    withSourcesJar()
}

// Add publishing configuration
publishing {
    publications {
        create<MavenPublication>("maven") {
            from(components["java"])
        }
    }
}