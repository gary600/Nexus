package xyz.gary600.nexusclasses.extension

import org.bukkit.entity.Player
import org.bukkit.inventory.Inventory
import org.bukkit.inventory.ItemStack
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.NexusClasses
import xyz.gary600.nexusclasses.PlayerData

// Extension functions/properties for Bukkit types to reduce code repetition


// Player
val Player.playerData: PlayerData
    get() = NexusClasses.instance!!.getPlayerData(uniqueId) // should never be called before plugin instantiated

var Player.nexusClass: NexusClass
    get() = this.playerData.nexusClass
    set(nexusClass) {this.playerData.nexusClass = nexusClass}

var Player.debugMessages: Boolean
    get() = this.playerData.debugMessages
    set(debugMessages) {this.playerData.debugMessages = debugMessages}

fun Player.sendDebugMessage(msg: String) {
    if (debugMessages) {
        sendMessage(msg)
    }
}


// ItemStack
fun ItemStack.isClassItem() =
    enchantments.containsKey(NexusClasses.instance?.classItemEnchantment) // returns False before plugin initalized