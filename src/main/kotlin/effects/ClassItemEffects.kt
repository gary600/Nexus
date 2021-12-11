package xyz.gary600.nexusclasses.effects

import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import xyz.gary600.nexusclasses.extension.itemNexusClass
import xyz.gary600.nexusclasses.extension.nexusClass
import xyz.gary600.nexusclasses.extension.nexusClassesEnabled
import xyz.gary600.nexusclasses.extension.nexusDebugMessage

/**
 * Miscellaneous effects to manage class items
 */
@Suppress("unused")
class ClassItemEffects : Effects() {
    // Prevent using a class item belonging to the wrong class or in a world with classes disabled
    @EventHandler
    fun preventUseWrongClassItem(event: PlayerInteractEvent) {
        // If item has a class and it's not the same as the player
        if (
            (event.item?.itemNexusClass != null && event.player.nexusClass != event.item!!.itemNexusClass)
            || !event.player.world.nexusClassesEnabled
        ) {
            event.item?.amount = 0 // delete the item
            event.isCancelled = true // cancel interaction
            event.player.nexusDebugMessage("Removed class item from wrong class or in wrong world")
        }
    }

    // Prevent dropping class items by that class, delete if dropped by another class
    @EventHandler
    fun preventDropClassItem(event: PlayerDropItemEvent) {
        if (
            event.itemDrop.itemStack.itemNexusClass != null
            && event.player.world.nexusClassesEnabled
        ) {
            // Players of that class can't drop the item
            if (event.player.nexusClass == event.itemDrop.itemStack.itemNexusClass) {
                event.isCancelled = true
                event.player.nexusDebugMessage("Prevented dropping class item")
            }
            // Other classes can drop to delete it
            else {
                event.itemDrop.remove()
                event.player.nexusDebugMessage("Removed class item from wrong class")
            }
        }
    }
    // Prevent putting class items in any other inventory
    @EventHandler
    fun preventMoveClassItem(event: InventoryClickEvent) {
        if (
            // If shift clicked from player's inventory
            ((
                event.click.isShiftClick
                && event.clickedInventory == event.whoClicked.inventory // inventory *is* the player's
                && event.currentItem?.itemNexusClass != null // item *under* cursor is the class item
            )
            // If item moved into other inventory normally
            || (
                event.clickedInventory != event.whoClicked.inventory // inventory is *not* the player's
                && event.cursor?.itemNexusClass != null // item *on* cursor is the class item
            ))
            && event.whoClicked.world.nexusClassesEnabled
        ) {
            event.isCancelled = true
        }
    }
    // Prevent dragging class items
    @EventHandler
    fun preventDragClassItem(event: InventoryDragEvent) {
        if (
            event.oldCursor.itemNexusClass != null
            && event.whoClicked.world.nexusClassesEnabled
        ) {
            event.isCancelled = true
        }
    }
}