import org.jetbrains.kotlin.gradle.dsl.JvmTarget

plugins {
	kotlin("jvm") version "2.1.0"
	kotlin("plugin.spring") version "2.1.0"
	id("org.springframework.boot") version "3.5.5"
	id("io.spring.dependency-management") version "1.1.7"
}

group = "com.jammking"
version = "0.0.1-SNAPSHOT"
description = "Bridge application applying WebProbe on TasteCompass"

java {
	toolchain {
		languageVersion = JavaLanguageVersion.of(17)
	}
}

kotlin {
	compilerOptions {
		jvmTarget.set(JvmTarget.JVM_17)
		freeCompilerArgs.addAll("-Xjsr305=strict")
	}
}

dependencies {
	// Spring starters
	implementation("org.springframework.boot:spring-boot-starter")
	implementation("org.springframework.boot:spring-boot-starter-web")
	implementation("org.springframework.boot:spring-boot-starter-validation")
	implementation("org.springframework.boot:spring-boot-starter-actuator")
	implementation("org.springframework.boot:spring-boot-starter-data-mongodb")
	implementation("org.springframework.boot:spring-boot-starter-aop")

	// Kotlin / Jackson
	implementation("com.fasterxml.jackson.module:jackson-module-kotlin")
	implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.9.0")

	// HTTP / HTML
	implementation("com.squareup.okhttp3:okhttp:4.12.0")
	implementation("org.jsoup:jsoup:1.17.2")

	// Resilience / Scheduling lock
	implementation("io.github.resilience4j:resilience4j-spring-boot3:2.2.0")
	implementation("net.javacrumbs.shedlock:shedlock-spring:5.13.0")
	implementation("net.javacrumbs.shedlock:shedlock-provider-mongo:5.13.0")

	// Test
	testImplementation("org.springframework.boot:spring-boot-starter-test")
	testImplementation("io.mockk:mockk:1.13.12")
	testImplementation("com.squareup.okhttp3:mockwebserver:4.12.0")
}

tasks.withType<Test> {
	useJUnitPlatform()
}

tasks.withType<JavaCompile> {
	options.encoding = "UTF-8"
}
