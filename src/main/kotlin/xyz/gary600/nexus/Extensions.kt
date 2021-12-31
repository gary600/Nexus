package xyz.gary600.nexus

import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import xyz.gary600.nexus.classes.NexusClass

// Extension functions/properties for Bukkit types to reduce code repetition

/**
 * Get this player's PlayerData. Loaded lazily from files as needed, and created if it doesn't exist yet
 */
val Player.playerData: PlayerData
    get() =
        Nexus.playerData[uniqueId] // Get from loaded data if loaded
        ?: PlayerData.load(uniqueId)?.also { // Load it if it exists but is unloaded
            Nexus.playerData[uniqueId] = it

            Nexus.logger.info("Loaded playerdata for player $displayName")
        }
        ?: PlayerData().also { // Otherwise, create and save new playerdata
            Nexus.playerData[uniqueId] = it // Store to loaded
            it.save(uniqueId) // Store to file

            Nexus.logger.info("Created new playerdata for player $displayName")
        }

/**
 * Get or set this player's class
 */
inline var Player.nexusClass: NexusClass
    get() = playerData.nexusClass
    set(x) {
        playerData.nexusClass = x
        playerData.save(uniqueId)
    }

/**
 * Get or set if this player is subscribed to debug messages
 */
inline var Player.nexusDebug: Boolean
    get() = playerData.debug
    set(x) {
        playerData.debug = x
        playerData.save(uniqueId)
    }

/**
 * Get or set this player's Corruption level
 */
inline var Player.corruption: Int
    //TODO: make scoreboard objective name configurable
    get() = scoreboard.getObjective("Corruption")?.getScore(name)?.score ?: 0
    set(x) {
        scoreboard.getObjective("Corruption")?.getScore(name)?.score = x
    }

inline val Player.corruptionTier: CorruptionTier
    get() = if (corruption >= 150) CorruptionTier.Tier3
            else when(corruption) {
                in 50..99 -> CorruptionTier.Tier1
                in 100..149 -> CorruptionTier.Tier2
                else -> CorruptionTier.Tier0
            }

enum class CorruptionTier { Tier0, Tier1, Tier2, Tier3 }

/**
 * Sends a debug message to the player if they're subscribed to them
 */
fun Player.nexusDebugMessage(msg: String) {
    if (nexusDebug) {
        nexusMessage("§oDEBUG:§r $msg")
    }
}

/**
 * Sends a Nexus-styled message
 */
fun CommandSender.nexusMessage(msg: String) {
    sendMessage("§6[Nexus]§r $msg")
}

/**
 * Get or set this ItemMeta's Nexus class
 */
var ItemMeta.itemNexusClass: NexusClass?
    get() {
        // If it doesn't have the tag or it isn't a String, null
        if (!persistentDataContainer.has(
            Nexus.classItemKey,
            PersistentDataType.STRING
        )) {
            return null
        }

        // Get the class item tag as string and parse it to as Nexusclass, fail silently
        return persistentDataContainer.get(
            Nexus.classItemKey,
            PersistentDataType.STRING
        )?.let(NexusClass::fromString) // Parse it to a NexusClass
    }
    set(x) {
        if (x == NexusClass.Mundane || x == null) {
            persistentDataContainer.remove(Nexus.classItemKey)
        }
        else {
            persistentDataContainer.set(
                Nexus.classItemKey,
                PersistentDataType.STRING,
                x.name
            )
        }
    }

/**
 * Get or set this ItemStack's Nexus class
 */
var ItemStack.itemNexusClass: NexusClass?
    get() = itemMeta?.itemNexusClass
    set(x) { itemMeta?.itemNexusClass = x }

/**
 * Get or set if Nexus is enabled in this world
 */
var World.nexusEnabled: Boolean
    get() = uid in Nexus.enabledWorlds
    set(x) {
        Nexus.enabledWorlds.let {
            when (x) {
                true -> it.add(uid)
                false -> it.remove(uid)
            }
        }
        Nexus.saveEnabledWorlds()
    }