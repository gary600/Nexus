package xyz.gary600.nexus

import co.aikar.commands.BukkitCommandManager
import org.bukkit.NamespacedKey
import org.bukkit.plugin.java.JavaPlugin
import xyz.gary600.nexus.effects.*
import java.io.File

/**
 * Nexus: custom plugin for CMURPGA's Nexus RP, implementing character classes
 * Most internal global API is located in the Nexus singleton.
 */
class NexusPlugin : JavaPlugin() {
    val classItemKey = NamespacedKey(this, "classitem")

    val playerDataFolder = File(dataFolder, "playerData")
    val enabledWorldsFile = File(dataFolder, "enabledWorlds.json")

    init {
        if (Nexus.plugin_internal != null) {
            throw IllegalStateException("Only one Nexus plugin instance may exist")
        }
        // Store singleton instance
        Nexus.plugin_internal = this
    }

    override fun onEnable() {
        // Load playerdata and worlds
        Nexus.loadWorlds()

        // ACF command manager
        val commandManager = BukkitCommandManager(this)

        // Register commands
        commandManager.registerCommand(ClassCommand)
        commandManager.registerCommand(NexusCommand)

        // Register effects
        BuilderEffects.register()
        MinerEffects.register()
        ArtistEffects.register()
        WarriorEffects.register()
        ClassItemEffects.register()
    }
}