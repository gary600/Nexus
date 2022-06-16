group = "xyz.gary600.nexus"
version = "0.3.1"


plugins {
    kotlin("jvm") version "1.6.21"
    kotlin("plugin.serialization") version "1.6.21"
    id("com.github.johnrengelman.shadow") version "7.1.0" // needed to build a fat jar w/ dependencies included
    idea // IntelliJ integration
}

java {
    toolchain {
        languageVersion.set(JavaLanguageVersion.of(17)) // Modern Minecraft requires Java 17
    }
}

// Places to fetch packages from
repositories {
    mavenCentral() // Central package directory, for misc packages
//    mavenLocal() // for net.minecraft.server, since it can't be distributed (must have built Spigot at least once!)
    maven("https://hub.spigotmc.org/nexus/content/repositories/snapshots/") // Spigot dependency
    maven("https://oss.sonatype.org/content/repositories/snapshots") // Spigot sub-dependency
    maven("https://repo.aikar.co/content/groups/aikar/") // ACF command framework
}

// Packages to fetch + compile
dependencies {
    implementation("org.jetbrains.kotlin:kotlin-stdlib:1.6.21") // Kotlin stdlib
    implementation("org.jetbrains.kotlin:kotlin-reflect:1.6.21") // Kotlin reflection
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.3.3") // Kotlin serialization
    compileOnly("org.spigotmc:spigot-api:1.19-R0.1-SNAPSHOT") // Spigot API (don't include in output)
//    compileOnly("org.spigotmc:spigot:1.19-R0.1-SNAPSHOT") // Spigot internals and net.minecraft.server (don't include in output)
    implementation("co.aikar:acf-bukkit:0.5.0-SNAPSHOT") // ACF command framework
//    implementation("org.apache.commons:commons-text:1.9") // string manipulation utils
}

tasks {
    shadowJar { // "fat jar" build
        // Relocate libraries inside plugin package to prevent conflict with other plugins that use them
        relocate("co.aikar.commands", "xyz.gary600.nexus.lib.aikar.commands") // ACF
        relocate("co.aikar.locales", "xyz.gary600.nexus.lib.aikar.locales") // ACF dependency
//        relocate("org.apache.commons", "xyz.gary600.nexus.lib.commons") // Apache Commons utils
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
    register<JavaExec>("runSpigot") {
        // make sure jar is built and copied into correct folder
        dependsOn(shadowJar)
        dependsOn(copyJar)

        classpath = files("$buildDir/server/spigot.jar") // include Spigot jar - automatically finds main class
        javaLauncher.set(rootProject.javaToolchains.launcherFor(java.toolchain)) // use the same java toolchain
        workingDir("$buildDir/server/") // run in server directory, to keep project tree clean
        standardInput = System.`in` // pipe stdin so console is accessible
        args("-nogui") // run without the default ugly server gui
    }
}