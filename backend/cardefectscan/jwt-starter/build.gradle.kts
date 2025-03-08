plugins {
	kotlin("jvm") version "2.1.0"
	kotlin("plugin.spring") version "2.1.0"
	id("io.spring.dependency-management") version "1.1.7"
	id("org.springframework.boot") version "3.5.0-SNAPSHOT" apply false
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
	maven { url = uri("https://repo.spring.io/milestone") }
	maven { url = uri("https://repo.spring.io/snapshot") }
}

dependencies {
	api("org.springframework.boot:spring-boot-starter-security")
	implementation("io.github.microutils:kotlin-logging:3.0.5")
	// https://mvnrepository.com/artifact/com.auth0/java-jwt
	implementation("com.auth0:java-jwt:4.5.0")

	implementation("org.springframework.boot:spring-boot-autoconfigure")
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.jetbrains.kotlin:kotlin-reflect")
	testImplementation("org.jetbrains.kotlin:kotlin-test-junit5")
	testRuntimeOnly("org.junit.platform:junit-platform-launcher")
}

kotlin {
	compilerOptions {
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

tasks.withType<Test> {
	useJUnitPlatform()
}

dependencyManagement {
	imports {
		mavenBom(org.springframework.boot.gradle.plugin.SpringBootPlugin.BOM_COORDINATES)
	}
}

