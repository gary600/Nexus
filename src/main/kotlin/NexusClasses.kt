package xyz.gary600.nexusclasses

import co.aikar.commands.BukkitCommandManager
import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
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
    // The collection of all player data
    private val playerData = HashMap<UUID, PlayerData>()
    // The list of worlds that classes operate on
    private val worlds = ArrayList<UUID>()

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

    // Helper function for debug messages
    fun sendDebugMessage(player: Player, message: String) {
        if (getPlayerData(player.uniqueId).debugMessages) {
            player.sendMessage(message)
        }
    }

    override fun onEnable() {
        // Load config
        val datalist = config.getMapList("playerData")
        for (data in datalist) {
            try {
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
        println("[NexusClasses] Loaded playerdata for ${playerData.size} players")

        // Register class item enchantment
        // Reflection tomfoolery to force Spigot to allow us to register a new enchant (apparently this is normal????)
        val acceptingNewField = Enchantment::class.java.getDeclaredField("acceptingNew")
        acceptingNewField.trySetAccessible()
        acceptingNewField.set(null, true)
        // Actually register
        val enchantKey = NamespacedKey(this, "classitem")
        val enchant = ClassItemEnchantment(enchantKey)
        Enchantment.registerEnchantment(enchant)

        // ACF command manager
        val commandManager = BukkitCommandManager(this)

        // Register command
        commandManager.registerCommand(ClassCommand(this, enchant))

        // Register event handler
        server.pluginManager.registerEvents(ClassesListener(this, enchant), this)

        // Start tasks
        BuilderSunlightWeaknessTask(this).runTaskTimer(this, 0, 20) // Once per second
        MinerNightVisionTask(this).runTaskTimer(this, 0, 10) // 2x per second
        WarriorIronAllergyTask(this).runTaskTimer(this, 0, 10)
        ArtistWaterAllergyTask(this).runTaskTimer(this, 0, 10)
        BuilderHelmetDegradeTask(this).runTaskTimer(this, 0, 1200) // Once per minute
    }
}