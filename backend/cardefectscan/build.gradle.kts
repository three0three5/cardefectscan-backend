plugins {
	kotlin("jvm") version "1.9.25"
	kotlin("plugin.spring") version "1.9.25"
	id("org.springframework.boot") version "3.5.0-SNAPSHOT"
	id("io.spring.dependency-management") version "1.1.7"
	kotlin("plugin.jpa") version "1.9.25"
	id("org.openapi.generator") version "7.11.0"
}

group = "ru.hse"
version = "0.1.0"

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

dependencies {
//	implementation("org.springframework.boot:spring-boot-starter-amqp")
//	implementation("org.springframework.boot:spring-boot-starter-data-jpa")
//	implementation("org.springframework.boot:spring-boot-starter-data-redis")
//	implementation("org.springframework.boot:spring-boot-starter-security")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlin:kotlin-reflect")

	implementation("io.github.microutils:kotlin-logging:3.0.5")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	// https://mvnrepository.com/artifact/org.openapitools/openapi-generator
	implementation("org.openapitools:openapi-generator:7.11.0") {
		exclude(group = "org.slf4j", module = "slf4j-simple")
	}
//	runtimeOnly("org.postgresql:postgresql")
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
//	testImplementation("org.springframework.amqp:spring-rabbit-test")
//	testImplementation("org.springframework.security:spring-security-test")
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

tasks.withType<Test> {
	useJUnitPlatform()
}

sourceSets {
	main {
		kotlin {
			srcDir("${layout.buildDirectory.get()}/generated/src")
		}
	}
}

task("cleanGenerated") {
	delete("${layout.buildDirectory.get()}/generated")
}

tasks.jar.configure {
	enabled = false
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

project.afterEvaluate {
	tasks.named("openApiGenerate").configure {
		dependsOn("cleanGenerated")
	}

	tasks.named("compileKotlin").configure {
		dependsOn("openApiGenerate")
	}
}
