group = "xyz.gary600.nexusclasses"
version = "0.1.1"


plugins {
    kotlin("jvm") version "1.6.0"
    id("com.github.johnrengelman.shadow") version "7.1.0" // Lets us build a fat jar w/ dependencies included
    idea // IntelliJ integration
}

repositories {
    mavenCentral() // Central package directory, for misc packages
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot dependency
    maven("https://oss.sonatype.org/content/repositories/snapshots") // for Spigot sub-dependency
    maven("https://repo.aikar.co/content/groups/aikar/") // ACF command framework
}

dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.0") // Kotlin stdlib (need to include in jar)
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.0") // Kotlin reflection
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT") // Spigot API (don't include in output)
    implementation("co.aikar:acf-bukkit:0.5.0-SNAPSHOT") // ACF Command framework
}

tasks {
    shadowJar {
        // Relocate ACF packages to prevent conflict with other plugins that use ACF
        relocate("co.aikar.commands", "xyz.gary600.nexusclasses.acf")
        relocate("co.aikar.locales", "xyz.gary600.nexusclasses.locales")
        //TODO: figure out if I should relocate the Kotlin stdlib too?
    }

    build {
        dependsOn(shadowJar) // for convenience, also build shadowJar when doing normal build
    }
}