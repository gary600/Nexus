package xyz.gary600.nexus

import org.bukkit.World
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

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
 * Get or set if this player is subscribed to debug messages
 */
inline var Player.nexusDebug: Boolean
    get() = playerData.debug
    set(x) {
        playerData.debug = x
        playerData.save(uniqueId)
    }

/**
 * Sends a debug message to the player if they're subscribed to them
 */
fun Player.nexusDebug(msg: String) {
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
 * Get or set if Nexus is enabled in this world
 */
var World.nexusEnabled: Boolean
    get() = uid in Nexus.config.enabledWorlds
    set(x) {
        Nexus.config.enabledWorlds.apply {
            when (x) {
                true -> add(uid)
                false -> remove(uid)
            }
        }
        Nexus.config.save()
    }