import java.net.URI
import java.nio.file.Paths

plugins {
    kotlin("jvm") version "2.1.0"
    kotlin("plugin.spring") version "2.1.0"
    id("org.springframework.boot") version "3.5.0-SNAPSHOT"
    id("io.spring.dependency-management") version "1.1.7"
    kotlin("plugin.jpa") version "1.9.25"
    id("org.openapi.generator") version "7.11.0"
    id("de.undercouch.download") version "5.6.0"
}

group = "ru.hse"
version = "0.1.0"

val AGENT_DOWNLOAD_PATH = "${layout.buildDirectory.get()}/libs/javaagent/opentelemetry-javaagent.jar"
val JAVAAGENT_VERSION = "2.13.3"
val OPENTELEMETRY_JAVAAGENT_URL = "https://github.com/open-telemetry/opentelemetry-java-instrumentation/releases/download/v$JAVAAGENT_VERSION/opentelemetry-javaagent.jar"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
    maven { url = uri("https://repo.spring.io/milestone") }
    maven { url = uri("https://repo.spring.io/snapshot") }
}

sourceSets {
    main {
        kotlin {
            srcDir("${layout.buildDirectory.get()}/generated/src")
        }
    }
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-data-jpa")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.springframework:spring-webflux:7.0.0-M2")
    implementation("org.hibernate.validator:hibernate-validator:8.0.1.Final")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")


    implementation("io.minio:minio:8.5.17")
    implementation("net.coobird:thumbnailator:0.4.20")

    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("org.springframework.boot:spring-boot-starter-actuator")

    implementation("org.openapitools:openapi-generator:7.11.0") {
        exclude(group = "org.slf4j", module = "slf4j-simple")
    }
    runtimeOnly("org.postgresql:postgresql")

    implementation(project(":jwt-starter"))

    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.amqp:spring-rabbit-test")
    testImplementation("org.springframework.security:spring-security-test")
    testImplementation("org.testcontainers:postgresql:1.20.0")
    testImplementation("com.redis:testcontainers-redis:2.2.3")
    testImplementation("org.springframework.boot:spring-boot-starter-test")

    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

allOpen {
    annotation("jakarta.persistence.Entity")
    annotation("jakarta.persistence.MappedSuperclass")
    annotation("jakarta.persistence.Embeddable")
}

tasks {
    withType<Test> {
        useJUnitPlatform()
    }

    jar.configure {
        enabled = false
    }

    compileKotlin.configure {
        dependsOn("openApiGenerateClient")
        dependsOn("openApiGenerate")
    }

    openApiGenerate {
        generatorName.set("kotlin-spring")
        inputSpec.set("$rootDir/src/main/resources/cardefectscan.yaml")
        outputDir.set("${layout.buildDirectory.get()}/generated")
        apiPackage.set("org.openapi.cardefectscan.api")
        modelPackage.set("org.openapi.cardefectscan.model")
        configOptions.set(
            mapOf(
                "useSpringBoot3" to "true",
                "interfaceOnly" to "true",
                "documentationProvider" to "none",
                "useTags" to "true",
            )
        )
    }
}

tasks.register("downloadOpenTelemetryJavaAgent") {
    val file = Paths.get(AGENT_DOWNLOAD_PATH).toFile()
    if (!file.exists()) {
        download {
            run {
                src { URI(OPENTELEMETRY_JAVAAGENT_URL).toURL() }
                dest { Paths.get(AGENT_DOWNLOAD_PATH).toFile() }
            }
        }
    }
}

tasks.register("cleanGenerated") {
    delete("${layout.buildDirectory.get()}/generated")
}

tasks.register("openApiGenerateClient", org.openapitools.generator.gradle.plugin.tasks.GenerateTask::class) {
    generatorName.set("kotlin")
    dependsOn("cleanGenerated")
    inputSpec.set("$rootDir/src/main/resources/model-service.yaml")
    outputDir.set("${layout.buildDirectory.get()}/generated")
    apiPackage.set("org.openapi.modelservice.api")
    modelPackage.set("org.openapi.modelservice.model")
    generateApiTests.set(false)
    generateModelTests.set(false)
    configOptions.set(
        mapOf(
            "library" to "jvm-okhttp4",
            "dateLibrary" to "java8",
            "serializationLibrary" to "jackson",
        )
    )
}

tasks.register("bootRunLocal") {
    group = "application"
    description = "Runs this project as a Spring Boot application with the dev profile"
    doFirst {
        tasks.bootRun.configure {
            systemProperty("spring.profiles.active", "local")
        }
    }
    finalizedBy("bootRun")
}
