@file:Suppress("unused")

package xyz.gary600.nexusclasses.effects

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.Particle
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
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
import xyz.gary600.nexusclasses.ClassItemEnchantment
import xyz.gary600.nexusclasses.NexusClass
import xyz.gary600.nexusclasses.NexusClasses

/**
 * The event listeners used by NexusClasses
 */
class ClassesListener(private val plugin: NexusClasses, private val classItemEnchantment: ClassItemEnchantment) : Listener {
    // Builder Perk: Inhibit fall damage [DONE]
    @EventHandler
    fun builderNoFallDamage(event: EntityDamageEvent) {
        val entity = event.entity
        if (
            entity is Player
            && plugin.getPlayerData(entity.uniqueId).nexusClass == NexusClass.Builder
            && event.cause == EntityDamageEvent.DamageCause.FALL
        ) {
            event.isCancelled = true
            plugin.sendDebugMessage(entity, "[NexusClasses] Builder perk: Fall damage cancelled!")
        }
    }

    // Builder Perk: Transmute blocks [DONE w/ changes]
    @EventHandler
    fun builderTransmute(event: PlayerInteractEvent) {
        // Only trigger when block right-clicked with a class-item stick in the primary hand
        if (
            event.action == Action.RIGHT_CLICK_BLOCK
            && event.hand == EquipmentSlot.HAND
            && event.player.inventory.getItem(event.player.inventory.heldItemSlot)?.type == Material.STICK // Change: only when holding a stick
            && event.player.inventory.getItem(event.player.inventory.heldItemSlot)?.enchantments?.containsKey(classItemEnchantment) == true
        ) {
            // Transmute if builder
            if (plugin.getPlayerData(event.player.uniqueId).nexusClass == NexusClass.Builder) {
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
                    block.world.spawnParticle(
                        Particle.BLOCK_DUST,
                        block.location.add(0.5, 0.5, 0.5),
                        32,
                        block.blockData
                    ) // Spawn particles at center of block
                    block.world.playSound(
                        block.location,
                        block.blockData.soundGroup.breakSound,
                        1.0f,
                        1.0f
                    ) // Play block break sound

                    plugin.sendDebugMessage(event.player, "[NexusClasses] Builder perk: Block transmuted!")
                }
            }
            // If not builder, delete item
            else {
                event.player.inventory.getItem(event.player.inventory.heldItemSlot)?.amount = 0
            }
        }
    }

    // Miner Perk: Certain ores additionally drop emerald
    @EventHandler
    fun minerFreeEmerald(event: BlockBreakEvent) {
        if (
            plugin.getPlayerData(event.player.uniqueId).nexusClass == NexusClass.Miner
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
            && event.player.gameMode != GameMode.CREATIVE // Don't drop for creative mode players
        ) {
            // We're not allowed to add items to the block drop list for some reason, so just drop it manually where the block is
            event.block.world.dropItemNaturally(event.block.location, ItemStack(Material.EMERALD, 1))
            plugin.sendDebugMessage(event.player, "[NexusClasses] Miner perk: Free emerald!")
        }
    }

    // Miner Weakness: Extra damage from zombies [DONE]
    @EventHandler
    fun minerZombieWeakness(event: EntityDamageByEntityEvent) {
        val entity = event.entity
        if (
            entity is Player
            && plugin.getPlayerData(entity.uniqueId).nexusClass == NexusClass.Miner
            && event.damager is Zombie
        ) {
            event.damage *= 1.2
            plugin.sendDebugMessage(entity, "[NexusClasses] Miner weakness: double damage from zombies!")
        }
    }

    // Warrior perk: Automatic fire aspect on golden weapons [DONE]
    @EventHandler
    fun warriorGoldWeapons(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        if (
            damager is Player
            && plugin.getPlayerData(damager.uniqueId).nexusClass == NexusClass.Warrior
            && damager.inventory.getItem(damager.inventory.heldItemSlot)?.type in arrayOf(
                Material.GOLDEN_SWORD,
                Material.GOLDEN_AXE
            )
        ) {
            event.entity.fireTicks = 80 // equivalent to Fire Aspect 1
            event.damage += 6 // equivalent to Strength II
            plugin.sendDebugMessage(damager, "[NexusClasses] Warrior perk: Enemy ignited!")
        }
    }

    // Warrior perk: Wearing gold armor gives fire immunity
    @EventHandler
    fun warriorFireResist(event: EntityDamageEvent) {
        val entity = event.entity
        if (
            entity is Player
            && plugin.getPlayerData(entity.uniqueId).nexusClass == NexusClass.Warrior
            && (
                entity.equipment?.helmet?.type == Material.GOLDEN_HELMET
                || entity.equipment?.chestplate?.type == Material.GOLDEN_CHESTPLATE
                || entity.equipment?.leggings?.type == Material.GOLDEN_LEGGINGS
                || entity.equipment?.boots?.type == Material.GOLDEN_BOOTS
            )
            && (
                event.cause == EntityDamageEvent.DamageCause.FIRE
                || event.cause == EntityDamageEvent.DamageCause.FIRE_TICK
                || event.cause == EntityDamageEvent.DamageCause.LAVA
            )
        ) {
            event.isCancelled = true
            plugin.sendDebugMessage(entity, "[NexusClasses] Warrior perk: Fire resistance!") // very spammy
        }
    }

    // Artist perk: free end pearl at all times
    @EventHandler
    fun artistFreeEndPearl(event: PlayerInteractEvent) {
        if (
            (event.action == Action.RIGHT_CLICK_AIR || event.action == Action.RIGHT_CLICK_BLOCK)
            && event.hand == EquipmentSlot.HAND
        ) {
            val classItem = event.player.inventory.getItem(event.player.inventory.heldItemSlot)
            if (
                classItem?.type == Material.ENDER_PEARL
                && classItem.enchantments.containsKey(classItemEnchantment)
                && event.player.getCooldown(Material.ENDER_PEARL) <= 0 // don't give pearl when on pearl cooldown
            ) {
                if (plugin.getPlayerData(event.player.uniqueId).nexusClass == NexusClass.Artist) {
                    classItem.amount = 2
                    plugin.sendDebugMessage(event.player, "[NexusClasses] Artist perk: free end pearl!")
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
        if (event.itemDrop.itemStack.enchantments.containsKey(classItemEnchantment)) {
            // Players of that class can't drop the item
            if (
                (
                    plugin.getPlayerData(event.player.uniqueId).nexusClass == NexusClass.Artist
                    && event.itemDrop.itemStack.type == Material.ENDER_PEARL
                )
                || (
                    plugin.getPlayerData(event.player.uniqueId).nexusClass == NexusClass.Builder
                    && event.itemDrop.itemStack.type == Material.STICK
                )
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
                && event.currentItem?.enchantments?.containsKey(classItemEnchantment) == true // item *under* cursor is the class item
            )
            // If item moved into other inventory normally
            || (
                event.clickedInventory != event.whoClicked.inventory // inventory is *not* the player's
                && event.cursor?.enchantments?.containsKey(classItemEnchantment) == true // item *on* cursor is the class item
            )
        ) {
            event.isCancelled = true
        }
    }
    // Prevent dragging class items
    @EventHandler
    fun preventDragClassItem(event: InventoryDragEvent) {
        if (event.oldCursor.enchantments.containsKey(classItemEnchantment)) {
            event.isCancelled = true
        }
    }
}