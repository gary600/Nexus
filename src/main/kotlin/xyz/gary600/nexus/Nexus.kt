package xyz.gary600.nexus

import java.lang.ClassCastException
import java.lang.IllegalArgumentException
import java.util.UUID

/**
 * Main Nexus singleton, containing most of the internal global API
 */
object Nexus {
    var plugin_internal: NexusPlugin? = null
        internal set

    val plugin: NexusPlugin
        get() = plugin_internal ?: throw IllegalStateException("Nexus plugin accessed before initalization")

    // Wrappers to Plugin stuff
    val classItemKey get() = plugin.classItemKey
    private val config get() = plugin.config
    private val logger get() = plugin.logger

    /**
     * The collection of Nexus player data
     */
    val playerData = HashMap<UUID, PlayerData>()

    /**
     * The set of worlds in which Nexus is enabled
     */
    val enabledWorlds = HashSet<UUID>()


    fun saveData() {
        // Format playerdata
        val pdOut = ArrayList<Map<String, Any>>()
        for ((uuid, pd) in playerData) {
            val map = pd.serialize().toMutableMap()
            map["uuid"] = uuid.toString()
            pdOut.add(map)
        }
        config.set("playerData", pdOut)

        // Format world list
        val worldsOut = enabledWorlds.map { it.toString() }
        config.set("worlds", worldsOut)

        plugin.saveConfig()
    }

    fun loadData() {
        // Load playerdata
        val configData = config.getMapList("playerData")
        for (data in configData) {
            try {
                @Suppress("UNCHECKED_CAST") // More knowledge about Kotlin required
                val pd = PlayerData.deserialize(data as Map<String, Any>)
                val uuid = UUID.fromString(data["uuid"] as String)
                playerData[uuid] = pd
            }
            // Skip player if it doesn't fit the format
            catch (x: IllegalArgumentException) {
                logger.warning("Skipping incorrectly formatted player UUID")
                continue
            }
            catch (x: ClassCastException) {
                logger.warning("Skipping badly parsed playerdata")
                continue
            }
        }
        logger.info("Loaded playerdata for ${playerData.size} players")

        // Load worlds
        val configWorlds = config.getStringList("worlds")
        for (world in configWorlds) {
            try {
                enabledWorlds.add(UUID.fromString(world))
            }
            catch (x: IllegalArgumentException) {
                logger.warning("Skipping incorrectly formatted world UUID")
            }
        }
        logger.info("Enabled Nexus in ${enabledWorlds.size} worlds")
    }
}