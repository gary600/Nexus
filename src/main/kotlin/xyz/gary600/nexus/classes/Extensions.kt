package xyz.gary600.nexus.classes

import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta
import org.bukkit.persistence.PersistentDataType
import xyz.gary600.nexus.Nexus
import xyz.gary600.nexus.playerData


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