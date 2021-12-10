package xyz.gary600.nexusclasses.effects

import org.bukkit.Material
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerDropItemEvent
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.extension.isClassItem
import xyz.gary600.nexusclasses.extension.nexusClass

/**
 * Miscellaneous effects
 */
@Suppress("unused")
class MiscEffects : Effects() {
    // Prevent dropping class items by that class, delete if dropped by another class
    @EventHandler
    fun preventDropClassItem(event: PlayerDropItemEvent) {
        if (event.itemDrop.itemStack.isClassItem) {
            // Players of that class can't drop the item
            if (
                (event.player.nexusClass == NexusClass.Artist && event.itemDrop.itemStack.type == Material.ENDER_PEARL)
                || (event.player.nexusClass == NexusClass.Builder && event.itemDrop.itemStack.type == Material.STICK)
            ) {
                event.isCancelled = true
            }
            // Other classes can drop to delete it
            else {
                event.itemDrop.remove()
            }
        }
    }
    // Prevent putting class items in any other inventory
    @EventHandler
    fun preventMoveClassItem(event: InventoryClickEvent) {
        if (
            // If shift clicked from player's inventory
            (
                event.click.isShiftClick
                && event.clickedInventory == event.whoClicked.inventory // inventory *is* the player's
                && event.currentItem?.isClassItem == true // item *under* cursor is the class item
            )
            // If item moved into other inventory normally
            || (
                event.clickedInventory != event.whoClicked.inventory // inventory is *not* the player's
                && event.cursor?.isClassItem == true // item *on* cursor is the class item
            )
        ) {
            event.isCancelled = true
        }
    }
    // Prevent dragging class items
    @EventHandler
    fun preventDragClassItem(event: InventoryDragEvent) {
        if (event.oldCursor.isClassItem) {
            event.isCancelled = true
        }
    }
}