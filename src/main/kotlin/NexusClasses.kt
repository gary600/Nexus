package xyz.gary600.nexusclasses

import co.aikar.commands.BukkitCommandManager
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.plugin.java.JavaPlugin
import xyz.gary600.nexusclasses.effects.*
import java.lang.ClassCastException
import java.lang.IllegalArgumentException
import java.util.*
import kotlin.collections.HashMap

/**
 * NexusClasses: custom character class plugin for CMURPGA's Nexus RP
 */
class NexusClasses : JavaPlugin() {
    // Class item enchantment
    val classItemEnchantment: ClassItemEnchantment = ClassItemEnchantment(NamespacedKey(this, "classitem"))

    // The collection of all player data
    private val playerData = HashMap<UUID, PlayerData>()
    // The list of worlds that classes operate on
//    private val worlds = ArrayList<UUID>()

    init {
        if (instance != null) {
            throw Exception("Only one NexusClasses instance may exist")
        }
        // Store singleton instance
        instance = this
    }

    fun getPlayerData(id: UUID): PlayerData {
        val data = playerData[id]

        // If there's no data for this player, create it and store it in the map
        return if (data == null) {
            val newData = PlayerData()
            playerData[id] = newData
            newData
        }
        // Otherwise return it
        else {
            data
        }
    }

    fun savePlayerData() {
        val out = ArrayList<Map<String, Any>>()
        for ((uuid, pd) in playerData) {
            val map: MutableMap<String, Any> = pd.serialize().toMutableMap()
            map["uuid"] = uuid.toString()
            out.add(map)
        }

        config.set("playerData", out)
        saveConfig()
    }

    override fun onEnable() {
        // Load config
        val datalist = config.getMapList("playerData")
        for (data in datalist) {
            try {
                @Suppress("UNCHECKED_CAST") // More knowledge about Kotlin required
                val pd = PlayerData.deserialize(data as Map<String, Any>)
                val uuid = UUID.fromString(data["uuid"] as String)
                playerData[uuid] = pd
            }
            // Skip player if it doesn't fit the format
            catch (x: IllegalArgumentException) {
                continue
            }
            catch (x: ClassCastException) {
                continue
            }
        }
        logger.info("Loaded playerdata for ${playerData.size} players")

        try {
            // Register class item enchantment
            // Reflection tomfoolery to force Spigot to allow us to register a new enchant (apparently this is normal????)
            val acceptingNewField = Enchantment::class.java.getDeclaredField("acceptingNew")
            acceptingNewField.trySetAccessible()
            acceptingNewField.set(null, true) // writing to a private field!!!
            // Actually register enchant
            Enchantment.registerEnchantment(classItemEnchantment)
        }
        catch (x: IllegalArgumentException) {
            logger.warning("Reload detected! Not re-registering enchantment, avoid using /reload")
        }

        // ACF command manager
        val commandManager = BukkitCommandManager(this)

        // Register command
        commandManager.registerCommand(ClassCommand())

        // Register effects
        BuilderEffects().register()
        MinerEffects().register()
        ArtistEffects().register()
        WarriorEffects().register()
        MiscEffects().register()
    }

    companion object {
        // Singleton instance
        var instance: NexusClasses? = null
            private set
    }
}