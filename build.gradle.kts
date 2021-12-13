group = "xyz.gary600.nexusclasses"
version = "0.2.0"


plugins {
    kotlin("jvm") version "1.6.0"
    id("com.github.johnrengelman.shadow") version "7.1.0" // needed to build a fat jar w/ dependencies included
    idea // IntelliJ integration
}

// Places to fetch packages from
repositories {
    mavenCentral() // Central package directory, for misc packages
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot dependency
    maven("https://oss.sonatype.org/content/repositories/snapshots") // Spigot sub-dependency
    maven("https://repo.aikar.co/content/groups/aikar/") // ACF command framework
}

// Packages to fetch + compile
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.0") // Kotlin stdlib
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.0") // Kotlin reflection
    compileOnly("org.spigotmc:spigot-api:1.17-R0.1-SNAPSHOT") // Spigot API (don't include in output)
    implementation("co.aikar:acf-bukkit:0.5.0-SNAPSHOT") // ACF command framework
}

tasks {
    shadowJar { // "fat jar" build
        // Relocate libraries inside plugin package to prevent conflict with other plugins that use them
        relocate("co.aikar.commands", "xyz.gary600.nexusclasses.aikar.commands") // ACF
        relocate("co.aikar.locales", "xyz.gary600.nexusclasses.aikar.locales") // ACF dependency
    }

    build {
        dependsOn(shadowJar) // for convenience, also build shadowJar when doing normal build
    }

    // Custom task: copy fat jar into Spigot plugin folder
    val copyJar by register<Copy>("copyJar") {
        dependsOn(shadowJar) // make sure jar is built
        from("$buildDir/libs/") // take from the libs output dir
        include("*-all.jar") // copy the fat jar
        into("$buildDir/server/plugins") // to server plugins folder
    }

    // Custom task: run Spigot
    register<Exec>("runSpigot") {
        // make sure jar is built and copied into correct folder
        dependsOn(shadowJar)
        dependsOn(copyJar)

        // run Spigot
        workingDir("$buildDir/server/")
        commandLine("java", "-jar", "$buildDir/server/spigot.jar")
    }
}