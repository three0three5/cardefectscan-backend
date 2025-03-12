plugins {
    kotlin("jvm") version "1.9.25"
    kotlin("plugin.spring") version "1.9.25"
    id("org.springframework.boot") version "3.4.3"
    id("io.spring.dependency-management") version "1.1.7"
    id("org.openapi.generator") version "7.11.0"
}

group = "ru.hse"
version = "0.0.1-SNAPSHOT"

java {
    toolchain {
        languageVersion = JavaLanguageVersion.of(21)
    }
}

repositories {
    mavenCentral()
}

dependencies {
    implementation("org.springframework.boot:spring-boot-starter-amqp")
    implementation("org.springframework.boot:spring-boot-starter-web")
    implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
    implementation("org.jetbrains.kotlin:kotlin-reflect")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")

    implementation("io.ktor:ktor-client-core:2.3.0")
    implementation("io.ktor:ktor-client-cio:2.3.0")

    implementation("io.minio:minio:8.5.17")
    implementation("io.github.microutils:kotlin-logging:3.0.5")
    implementation("org.openapitools:openapi-generator:7.11.0") {
        exclude(group = "org.slf4j", module = "slf4j-simple")
    }

    testImplementation("org.springframework.boot:spring-boot-starter-test")
    testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
    testImplementation("org.springframework.amqp:spring-rabbit-test")
    testRuntimeOnly("org.junit.platform:junit-platform-launcher")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.8.1")
}

task("cleanGenerated") {
    delete("${layout.buildDirectory.get()}/generated")
}

sourceSets {
    main {
        kotlin {
            srcDir("${layout.buildDirectory.get()}/generated/src")
        }
    }
}

openApiGenerate {
    generatorName.set("kotlin-spring")
    inputSpec.set("$rootDir/src/main/resources/model-service.yaml")
    outputDir.set("${layout.buildDirectory.get()}/generated")
    apiPackage.set("org.openapi.modelservice.api")
    modelPackage.set("org.openapi.modelservice.model")
    configOptions.set(
        mapOf(
            "useSpringBoot3" to "true",
            "interfaceOnly" to "true",
            "documentationProvider" to "none",
            "useTags" to "true",
        )
    )
}

project.afterEvaluate {
    tasks.named("openApiGenerate").configure {
        dependsOn("cleanGenerated")
    }
    tasks.named("compileKotlin").configure {
        dependsOn("openApiGenerate")
    }
}

kotlin {
    compilerOptions {
        freeCompilerArgs.addAll("-Xjsr305=strict")
    }
}

tasks.withType<Test> {
    useJUnitPlatform()
}
