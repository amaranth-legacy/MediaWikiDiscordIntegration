plugins {
	id("java")
	application
	kotlin("jvm")
	kotlin("plugin.serialization") version "2.1.0"
	id("io.ktor.plugin") version "3.1.0"
}

group = "community.amaranth_legacy.mediawiki_discord_integration"
version = "0.2.1"

repositories {
	mavenCentral()
}

dependencies {
	testImplementation(platform("org.junit:junit-bom:5.10.0"))
	testImplementation("org.junit.jupiter:junit-jupiter")

	// Kord
	implementation("dev.kord:kord-core:0.15.0")
	// Logback
	implementation("ch.qos.logback:logback-classic:1.5.16")
	// kotlin-logging
	implementation("io.github.oshai:kotlin-logging-jvm:7.0.3")
	// Ktor client
	implementation("io.ktor:ktor-client-core")
	implementation("io.ktor:ktor-client-cio")
	// Ktor server
	implementation("io.ktor:ktor-server-core")
	implementation("io.ktor:ktor-server-cio")
	implementation("io.ktor:ktor-server-content-negotiation")
	// Ktor kotlinx-serialization
	implementation("io.ktor:ktor-serialization-kotlinx-json")
	// kotlinx-datetime
	implementation("org.jetbrains.kotlinx:kotlinx-datetime:0.6.2")
	// libsodium
	implementation("com.ionspin.kotlin:multiplatform-crypto-libsodium-bindings:0.9.2")
}

kotlin {
	jvmToolchain(21)
}

application {
	mainClass = "community.amaranth_legacy.mediawiki_discord_integration.MainKt"
}

tasks.test {
	useJUnitPlatform()
}

