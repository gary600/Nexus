package xyz.gary600.nexus

import kotlinx.serialization.decodeFromString
import kotlinx.serialization.encodeToString
import kotlinx.serialization.json.Json
import java.lang.IllegalArgumentException
import java.util.UUID

/**
 * Main Nexus singleton, containing much of the internal global API
 */
object Nexus {
    var plugin_internal: NexusPlugin? = null
        internal set

    val plugin: NexusPlugin
        get() = plugin_internal ?: throw IllegalStateException("Nexus plugin accessed before initalization")

    // Wrappers to Plugin stuff
    val classItemKey get() = plugin.classItemKey
    val logger get() = plugin.logger

    // JSON serializer
    internal val json = Json {
        prettyPrint = true
        ignoreUnknownKeys = true
    }

    /**
     * The collection of Nexus player data
     */
    val playerData = HashMap<UUID, PlayerData>()

    /**
     * The set of worlds in which Nexus is enabled
     */
    val enabledWorlds = HashSet<UUID>()

    /**
     * Load the set of enabled worlds from the file
     */
    fun loadWorlds() {
        enabledWorlds.clear()

        // If file doesn't exist, just clear
        if (!plugin.enabledWorldsFile.exists()) {
            return
        }

        // Otherwise, load
        //TODO: Handle deserialization exceptions
        enabledWorlds += json.decodeFromString<HashSet<String>>(plugin.enabledWorldsFile.readText()).mapNotNull {
            try {
                UUID.fromString(it)
            } catch (x: IllegalArgumentException) {
                logger.warning("Skipping invalid world UUID")
                null
            }
        }
    }

    /**
     * Save the set of enabled worlds to the file
     */
    fun saveWorlds() {
        // Create data folder if it doesn't exist
        if (!plugin.dataFolder.exists()) {
            plugin.dataFolder.mkdirs()
        }

        plugin.enabledWorldsFile.writeText(json.encodeToString(enabledWorlds.map { it.toString() }))
    }
}