pluginManagement {
	plugins {
		kotlin("jvm") version "2.1.10"
		kotlin("plugin.serialization") version "2.1.10"
		id("io.ktor.plugin") version "3.1.0"
	}
}

plugins {
	id("org.gradle.toolchains.foojay-resolver-convention") version "0.8.0"
}

rootProject.name = "MediaWikiDiscordIntegration"

