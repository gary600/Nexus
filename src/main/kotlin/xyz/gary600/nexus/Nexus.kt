package xyz.gary600.nexus

import co.aikar.commands.BukkitCommandManager
import kotlinx.serialization.SerializationException
import kotlinx.serialization.json.Json
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import xyz.gary600.nexus.classes.ClassCommand
import xyz.gary600.nexus.classes.effects.*
import xyz.gary600.nexus.corruption.CorruptionEffects
import java.io.File
import java.util.UUID
import java.util.concurrent.ConcurrentHashMap

/**
 * Nexus plugin class, also exposing much of the internal global API via a companion object
 */
class Nexus : JavaPlugin() {
    // Properties that require the plugin to be loaded to be used
    val classItemKey = NamespacedKey(this, "class_item")
    val playerDataFolder = File(dataFolder, "playerData")
    val configFile = File(dataFolder, "config.json")

    init {
        if (plugin_internal != null) {
            throw IllegalStateException("Only one Nexus plugin instance may exist")
        }
        // Store singleton instance
        plugin_internal = this
    }

    override fun onEnable() {
        // Load config
        loadConfig()

        // ACF command manager
        val commandManager = BukkitCommandManager(this)

        // Register commands
        commandManager.registerCommand(ClassCommand)
        commandManager.registerCommand(NexusCommand)

        // Register effects
        MiscEffects.register()
        // Classes module
        BuilderEffects.register()
        MinerEffects.register()
        ArtistEffects.register()
        WarriorEffects.register()
        ClassItemEffects.register()
        // Corruption module
        CorruptionEffects.register()
    }

    // Singleton features
    companion object {
        // Internal nullable plugin instance reference: null before plugin instantiated
        private var plugin_internal: Nexus? = null

        /**
         * Get the plugin instance. Throws IllegalStateException if called before the plugin is instantiated
         */
        val plugin: Nexus
            get() = plugin_internal ?: throw IllegalStateException("Nexus plugin accessed before initalization")

        // Wrappers to Plugin stuff
        val classItemKey get() = plugin.classItemKey
        val logger get() = plugin.logger

        // JSON serializer
        val json = Json {
            prettyPrint = true
            ignoreUnknownKeys = true
        }

        /**
         * Global Nexus configuration, such as enabled worlds
         */
        var config = Config()
            private set

        /**
         * The collection of Nexus player data. Needs to be thread-safe
         */
        val playerData = ConcurrentHashMap<UUID, PlayerData>()

        fun loadConfig() {
            try {
                config = Config.load()
            }
            catch (x: SerializationException) {
                logger.severe("Failed to load config, using defaults")
            }
        }
    }
}