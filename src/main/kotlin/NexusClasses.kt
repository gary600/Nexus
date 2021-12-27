package xyz.gary600.nexusclasses

import co.aikar.commands.BukkitCommandManager
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import xyz.gary600.nexusclasses.effects.*
import java.lang.ClassCastException
import java.lang.IllegalArgumentException
import java.util.UUID
import kotlin.collections.HashMap

/**
 * NexusClasses: custom character class plugin for CMURPGA's Nexus RP
 */
class NexusClasses : JavaPlugin() {
    val classItemKey = NamespacedKey(this, "classitem")

    // The collection of all player data
    val playerData = HashMap<UUID, PlayerData>()
    // The list of worlds that classes effects work in
    val worlds = HashSet<UUID>()

    init {
        if (instance_internal != null) {
            throw Exception("Only one NexusClasses instance may exist")
        }
        // Store singleton instance
        instance_internal = this
    }

    fun saveData() {
        // Format playerdata
        val pdOut = ArrayList<Map<String, Any>>()
        for ((uuid, pd) in playerData) {
            val map: MutableMap<String, Any> = pd.serialize().toMutableMap()
            map["uuid"] = uuid.toString()
            pdOut.add(map)
        }
        config.set("playerData", pdOut)

        // Format world list
        val worldsOut = worlds.map { it.toString() }
        config.set("worlds", worldsOut)

        saveConfig()
    }

    override fun onEnable() {
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
                worlds.add(UUID.fromString(world))
            }
            catch (x: IllegalArgumentException) {
                logger.warning("Skipping incorrectly formatted world UUID")
            }
        }
        logger.info("Enabled class effects in ${worlds.size} worlds")

        // ACF command manager
        val commandManager = BukkitCommandManager(this)

        // Register command
        commandManager.registerCommand(ClassCommand())

        // Register effects
        BuilderEffects().register()
        MinerEffects().register()
        ArtistEffects().register()
        WarriorEffects().register()
        ClassItemEffects().register()
    }

    companion object {
        // Singleton instance
        // Note: cannot use Kotlin-style singleton because Bukkit's API requires a constructor to exist
        var instance_internal: NexusClasses? = null
            private set

        // Wrapper to clean up plugin references: since nothing should ever interact with the singleton instance
        // before the plugin is instantiated, it's fine to throw an NPE if something does
        val instance: NexusClasses
            get() = instance_internal ?: throw NullPointerException("NexusClasses has not yet been instantiated")
    }
}