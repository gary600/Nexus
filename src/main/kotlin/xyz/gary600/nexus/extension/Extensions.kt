package xyz.gary600.nexus.extension

import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import xyz.gary600.nexus.NexusClass
import xyz.gary600.nexus.Nexus
import xyz.gary600.nexus.PlayerData

// Extension functions/properties for Bukkit types to reduce code repetition

/**
 * Tracks this player's PlayerData
 */
val Player.nexusPlayerData: PlayerData
    // Get this player's PlayerData, or create it if it doesn't exist
    get() {
        val data = Nexus.instance.playerData[uniqueId]

        // If there's no data for this player, create it and store it in the map
        return if (data == null) {
            val newData = PlayerData()
            Nexus.instance.playerData[uniqueId] = newData
            newData
        }
        // Otherwise return it
        else {
            data
        }
    }

/**
 * Tracks this player's class
 */
var Player.nexusClass: NexusClass
    get() = this.nexusPlayerData.nexusClass
    set(x) {this.nexusPlayerData.nexusClass = x}

/**
 * Tracks if this player is subscribed to debug messages
 */
var Player.debugMessages: Boolean
    get() = this.nexusPlayerData.debugMessages
    set(x) {this.nexusPlayerData.debugMessages = x}

/**
 * Sends a debug message to the player if they're subscribed to them
 */
fun Player.nexusDebugMessage(msg: String) {
    if (debugMessages) {
        nexusMessage(msg)
    }
}

/**
 * Sends a Nexus-styled message
 */
fun CommandSender.nexusMessage(msg: String) {
    sendMessage("§6[Nexus]§r $msg")
}

/**
 * Tracks if this ItemMeta marks a class item
 */
var ItemMeta.itemNexusClass: NexusClass?
    get() =
        persistentDataContainer.get( // Get the class item tag
            Nexus.instance.classItemKey,
            PersistentDataType.BYTE
        )
            ?.let(NexusClass::fromByte) // Parse it to a NexusClass
    set(x) {
        persistentDataContainer.set(
            Nexus.instance.classItemKey,
            PersistentDataType.BYTE,
            x?.toByte() ?: 0 // 0 if Mundane
        )
    }

/**
 * Tracks if this ItemStack is a class item
 */
var ItemStack.itemNexusClass: NexusClass? // wrapper around ItemMeta property
    get() = itemMeta?.itemNexusClass
    set(x) { itemMeta?.itemNexusClass = x }

/**
 * Tracks if class effects are enabled in this world
 */
var World.nexusEnabled: Boolean
    get() = this.uid in Nexus.instance.worlds
    set(x) {
        Nexus.instance.worlds.let {
            when (x) {
                true -> it.add(this.uid)
                false -> it.remove(this.uid)
            }
        }
    }