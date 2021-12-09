package xyz.gary600.nexusclasses.extension

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.NexusClasses
import xyz.gary600.nexusclasses.PlayerData

// Extension functions/properties for Bukkit types to reduce code repetition


// Player
val Player.playerData: PlayerData
    get() = NexusClasses.instance!!.getPlayerData(uniqueId) // should never be called before plugin instantiated

var Player.nexusClass: NexusClass
    get() = this.playerData.nexusClass
    set(x) {this.playerData.nexusClass = x}

var Player.debugMessages: Boolean
    get() = this.playerData.debugMessages
    set(x) {this.playerData.debugMessages = x}

fun Player.sendDebugMessage(msg: String) {
    if (debugMessages) {
        sendMessage(msg)
    }
}


// ItemStack
var ItemStack.isClassItem: Boolean
    get() =
        itemMeta?.persistentDataContainer?.get(
            NexusClasses.instance!!.classItemKey,
            PersistentDataType.BYTE
        ) != 0.toByte()
    set(x) {
        val meta = itemMeta
        meta?.persistentDataContainer?.set(
            NexusClasses.instance!!.classItemKey,
            PersistentDataType.BYTE,
            when (x) { // Boolean.toByte() only exists on native???
                true -> 1
                false -> 0
            }.toByte()
        )
        itemMeta = meta
    }