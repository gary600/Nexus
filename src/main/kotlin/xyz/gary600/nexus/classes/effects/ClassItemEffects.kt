package xyz.gary600.nexus.classes.effects

import org.bukkit.entity.Player
import org.bukkit.event.EventHandler
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import xyz.gary600.nexus.Effects
import xyz.gary600.nexus.itemNexusClass
import xyz.gary600.nexus.nexusClass
import xyz.gary600.nexus.nexusEnabled
import xyz.gary600.nexus.nexusDebugMessage

/**
 * Miscellaneous effects to manage class items
 */
@Suppress("unused")
object ClassItemEffects : Effects() {
    // Prevent using a class item belonging to the wrong class or in a world with classes disabled
    @EventHandler
    fun preventUseWrongClassItem(event: PlayerInteractEvent) {
        // If item has a class AND (it's not the same as the player's OR the player is in a non-class world)
        if (
            event.item?.itemNexusClass != null
            && (
                event.player.nexusClass != event.item!!.itemNexusClass
                || !event.player.world.nexusEnabled
            )
        ) {
            event.item?.amount = 0 // delete the item
            event.isCancelled = true // cancel interaction
            event.player.nexusDebugMessage("Removed class item from wrong class or in wrong world")
        }
    }

    // Prevent dropping class items by that class, delete if dropped by another class
    @EventHandler
    fun preventDropClassItem(event: PlayerDropItemEvent) {
        // If item has a class AND the player is in a class world
        if (
            event.itemDrop.itemStack.itemNexusClass != null
            && event.player.world.nexusEnabled
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
        // If (item is shift-clicked OR item is normally moved) AND player is in a class world
        if (
            // If shift clicked from player's inventory
            //TODO: Allow shift-clicking into armor slots
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
            //TODO: Also disable moving via number keys
            && event.whoClicked.world.nexusEnabled
        ) {
            event.isCancelled = true
            (event.whoClicked as? Player)?.nexusDebugMessage("Prevented moving class item")
        }
    }
    // Prevent dragging class items
    @EventHandler
    fun preventDragClassItem(event: InventoryDragEvent) {
        if (
            event.oldCursor.itemNexusClass != null
            && event.whoClicked.world.nexusEnabled
        ) {
            event.isCancelled = true
            (event.whoClicked as? Player)?.nexusDebugMessage("Prevented dragging class item")
        }
    }
}