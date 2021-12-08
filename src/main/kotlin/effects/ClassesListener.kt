@file:Suppress("unused")

package xyz.gary600.nexusclasses.effects

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.EventPriority
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.*
import org.bukkit.event.inventory.InventoryClickEvent
import org.bukkit.event.inventory.InventoryDragEvent
import org.bukkit.event.player.PlayerDropItemEvent
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.extension.isClassItem
import xyz.gary600.nexusclasses.extension.nexusClass
import xyz.gary600.nexusclasses.extension.sendDebugMessage

/**
 * The event listeners used by NexusClasses
 */
class ClassesListener : Listener {
    // Builder Perk: Inhibit fall damage
    @EventHandler(priority = EventPriority.HIGHEST)
    fun builderNoFallDamage(event: EntityDamageEvent) {
        val entity = event.entity
        if (
            entity is Player
            && entity.nexusClass == NexusClass.Builder
            && event.cause == EntityDamageEvent.DamageCause.FALL
        ) {
            event.isCancelled = true
            entity.sendDebugMessage("[NexusClasses] Builder perk: Fall damage cancelled!")
        }
    }

    // Builder Perk: Transmute blocks [DONE w/ changes]
    @EventHandler
    fun builderTransmute(event: PlayerInteractEvent) {
        // Only trigger when block right-clicked with a class-item stick in the primary hand
        if (
            event.action == Action.RIGHT_CLICK_BLOCK
            && event.hand == EquipmentSlot.HAND
            && event.player.inventory.getItem(event.player.inventory.heldItemSlot).let {
                it?.type == Material.STICK && it.isClassItem()
            }
        ) {
            // Transmute if builder
            if (event.player.nexusClass == NexusClass.Builder) {
                var transmuted = true
                val block = event.clickedBlock!! // cannot be null because of action type
                block.type = when (block.type) {
                    // Series 1: cobble -> stone -> stone brick -> obsidian
                    Material.COBBLESTONE -> Material.STONE
                    Material.STONE -> Material.STONE_BRICKS
                    Material.STONE_BRICKS -> Material.OBSIDIAN

                    // Series 2: deepslate -> tuff -> nether brick -> blackstone -> obsidian
                    Material.DEEPSLATE -> Material.TUFF
                    Material.TUFF -> Material.NETHER_BRICKS
                    Material.NETHER_BRICKS -> Material.BLACKSTONE
                    Material.BLACKSTONE -> Material.OBSIDIAN

                    // Otherwise keep it the same
                    else -> {
                        transmuted = false
                        block.type
                    }
                }
                if (transmuted) {
                    // Spawn particles at center of block
                    block.world.spawnParticle(
                        Particle.BLOCK_DUST,
                        block.location.add(0.5, 0.5, 0.5),
                        32,
                        block.blockData
                    )
                    // Play block break sound
                    block.world.playSound(
                        block.location,
                        block.blockData.soundGroup.breakSound,
                        1.0f,
                        1.0f
                    )

                    event.player.sendDebugMessage("[NexusClasses] Builder perk: Block transmuted!")
                }
            }
            // If not builder, delete item
            else {
                event.player.inventory.itemInMainHand.amount = 0
            }
        }
    }

    // Miner Perk: Certain ores additionally drop emerald
    @EventHandler
    fun minerFreeEmerald(event: BlockBreakEvent) {
        if (
            event.player.nexusClass == NexusClass.Miner
            && event.player.gameMode != GameMode.CREATIVE // Don't drop for creative mode players
            && event.block.type in arrayOf(
                Material.GOLD_ORE,
                Material.DEEPSLATE_GOLD_ORE,
                Material.LAPIS_ORE,
                Material.DEEPSLATE_LAPIS_ORE,
                Material.REDSTONE_ORE,
                Material.DEEPSLATE_REDSTONE_ORE,
                Material.DIAMOND_ORE,
                Material.DEEPSLATE_DIAMOND_ORE
            )
        ) {
            // We're not allowed to add items to the block drop list for some reason, so just drop it manually where the block is
            event.block.world.dropItemNaturally(event.block.location, ItemStack(Material.EMERALD, 1))
            event.player.sendDebugMessage("[NexusClasses] Miner perk: Free emerald!")
        }
    }

    // Miner Weakness: Extra damage from zombies [DONE]
    @EventHandler
    fun minerZombieWeakness(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        if (
            entity is Player
            && entity.nexusClass == NexusClass.Miner
            && event.damager is Zombie
        ) {
            event.damage *= 1.2
            entity.sendDebugMessage("[NexusClasses] Miner weakness: double damage from zombies!")
        }
    }

    // Warrior perk: Automatic fire aspect on golden weapons [DONE]
    @EventHandler
    fun warriorGoldWeapons(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        if (
            damager is Player
            && damager.nexusClass == NexusClass.Warrior
            && damager.inventory.itemInMainHand.type in arrayOf(
                Material.GOLDEN_SWORD,
                Material.GOLDEN_AXE
            )
        ) {
            event.entity.fireTicks = 80 // equivalent to Fire Aspect 1
            event.damage += 6 // equivalent to Strength II
            damager.sendDebugMessage("[NexusClasses] Warrior perk: Enemy ignited!")
        }
    }

    // Warrior perk: Wearing gold armor gives fire immunity
    @EventHandler
    fun warriorFireResist(event: EntityDamageEvent) {
        val entity = event.entity
        if (
            entity is Player
            && entity.nexusClass == NexusClass.Warrior
            && (
                entity.equipment?.helmet?.type == Material.GOLDEN_HELMET
                || entity.equipment?.chestplate?.type == Material.GOLDEN_CHESTPLATE
                || entity.equipment?.leggings?.type == Material.GOLDEN_LEGGINGS
                || entity.equipment?.boots?.type == Material.GOLDEN_BOOTS
            )
            && event.cause in arrayOf(
                EntityDamageEvent.DamageCause.FIRE,
                EntityDamageEvent.DamageCause.FIRE_TICK,
                EntityDamageEvent.DamageCause.LAVA
            )
        ) {
            event.isCancelled = true
            entity.sendDebugMessage("[NexusClasses] Warrior perk: Fire resistance!") // very spammy
        }
    }

    // Artist perk: free end pearl at all times
    //TODO: prevent giving when clicking a chest/other interactable block
    @EventHandler
    fun artistFreeEndPearl(event: PlayerInteractEvent) {
        if (event.action in arrayOf(Action.RIGHT_CLICK_AIR, Action.RIGHT_CLICK_BLOCK)) {
            val classItem = event.item
            if (
                classItem?.type == Material.ENDER_PEARL
                && classItem.isClassItem()
                && event.player.getCooldown(Material.ENDER_PEARL) <= 0 // don't give pearl when on pearl cooldown
            ) {
                if (event.player.nexusClass == NexusClass.Artist) {
                    classItem.amount = 2
                    event.player.sendDebugMessage("[NexusClasses] Artist perk: free end pearl!")
                }
                // Don't let non-Artists use the pearl
                else {
                    classItem.amount = 0
                    event.isCancelled = true
                }
            }
        }
    }

    // Artist weakness in ArtistWaterAllergyTask

    // Prevent dropping class items by that class, delete if dropped by another class
    @EventHandler
    fun preventDropClassItem(event: PlayerDropItemEvent) {
        if (event.itemDrop.itemStack.isClassItem()) {
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
                && event.currentItem?.isClassItem() == true // item *under* cursor is the class item
            )
            // If item moved into other inventory normally
            || (
                event.clickedInventory != event.whoClicked.inventory // inventory is *not* the player's
                && event.cursor?.isClassItem() == true // item *on* cursor is the class item
            )
        ) {
            event.isCancelled = true
        }
    }
    // Prevent dragging class items
    @EventHandler
    fun preventDragClassItem(event: InventoryDragEvent) {
        if (event.oldCursor.isClassItem()) {
            event.isCancelled = true
        }
    }
}