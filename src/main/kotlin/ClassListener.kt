@file:Suppress("unused")

package xyz.gary600.nexusclasses

import org.bukkit.GameMode
import org.bukkit.Material
import org.bukkit.entity.Player
import org.bukkit.entity.Zombie
import org.bukkit.event.EventHandler
import org.bukkit.event.Listener
import org.bukkit.event.block.Action
import org.bukkit.event.block.BlockBreakEvent
import org.bukkit.event.entity.*
import org.bukkit.event.player.PlayerInteractEvent
import org.bukkit.event.player.PlayerItemConsumeEvent
import org.bukkit.inventory.EquipmentSlot
import org.bukkit.inventory.ItemStack

class ClassListener(private val plugin: NexusClasses, private val freePearlEnchantment: FreePearlEnchantment) : Listener {
    // Builder Perk: Inhibit fall damage [DONE]
    @EventHandler
    fun builderNoFallDamage(event: EntityDamageEvent) {
        if (event.cause == EntityDamageEvent.DamageCause.FALL) {
            event.isCancelled = true
            event.entity.sendMessage("[NexusClasses] Builder perk: Fall damage cancelled!")
        }
    }

    // Builder Perk: Transmute blocks [DONE w/ changes]
    //TODO: only when holding a stick
    @EventHandler
    fun builderTransmute(event: PlayerInteractEvent) {
        // Only trigger when block right-clicked with primary hand
        if (event.action == Action.RIGHT_CLICK_BLOCK && event.hand == EquipmentSlot.HAND) {
            var transmuted = true
            event.clickedBlock!!.type = when (event.clickedBlock!!.type) { // cannot be null because of action type
                // Cycle 1: cobble -> stone -> stone brick -> obsidian
                Material.COBBLESTONE -> Material.STONE
                Material.STONE -> Material.STONE_BRICKS
                Material.STONE_BRICKS -> Material.OBSIDIAN
                Material.OBSIDIAN -> Material.COBBLESTONE

                // Cycle 2: deepslate -> tuff -> nether brick -> blackstone -> obsidian???? [talk to Sarah]
                Material.DEEPSLATE -> Material.TUFF
                Material.TUFF -> Material.NETHER_BRICKS
                Material.NETHER_BRICKS -> Material.BLACKSTONE
                Material.BLACKSTONE -> Material.DEEPSLATE

                // Otherwise keep it the same
                else -> {
                    transmuted = false
                    event.clickedBlock!!.type
                }
            }
            if (transmuted) {
                event.player.sendMessage("[NexusClasses] Builder perk: Block transmuted!")
            }
        }
    }

    // Builder Weakness: Burn in sun w/o helmet
    //TODO

    // Miner Perk: Certain ores additionally drop emerald
    @EventHandler
    fun minerFreeEmerald(event: BlockBreakEvent) {
        if (
            event.block.type in arrayOf(Material.GOLD_ORE, Material.LAPIS_ORE, Material.REDSTONE_ORE, Material.IRON_ORE)
            && event.player.gameMode != GameMode.CREATIVE // Don't drop for creative mode players
        ) {
            // We're not allowed to add items to the block drop list for some reason, so just drop it manually where the block is
            event.block.world.dropItemNaturally(event.block.location, ItemStack(Material.EMERALD, 1))
            event.player.sendMessage("[NexusClasses] Miner perk: Free emerald!")
        }
    }

    // Miner Perk: Night vision below y=60
    //TODO

    // Miner Weakness: Extra damage from zombies [DONE]
    @EventHandler
    fun minerExtraZombieDamage(event: EntityDamageByEntityEvent) {
        if (event.entity is Player && event.damager is Zombie) {
            event.damage *= 2 // Double damage
            event.entity.sendMessage("[NexusClasses] Miner weakness: double damage from zombies!")
        }
    }

    // Warrior perk: Automatic fire aspect on golden weapons [DONE]
    @EventHandler
    fun warriorFireAspect(event: EntityDamageByEntityEvent) {
        val damager = event.damager
        if (
            damager is Player
            && damager.inventory.getItem(damager.inventory.heldItemSlot)?.type in arrayOf(
                Material.GOLDEN_SWORD,
                Material.GOLDEN_AXE,
                Material.GOLDEN_PICKAXE,
                Material.GOLDEN_SHOVEL,
                Material.GOLDEN_HOE
            )
        ) {
            event.entity.fireTicks = 80 // equivalent to Fire Aspect 1
            event.damager.sendMessage("[NexusClasses] Warrior perk: Enemy ignited!")
        }
    }

    // Warrior perk: Holding gold weapons gives strength II
    //TODO

    // Warrior perk: Wearing gold armor gives fire immunity [DONE w/ changes]
    @EventHandler
    fun warriorFireResist(event: EntityDamageEvent) {
        if (
            event.entity is Player
            && (
                event.cause == EntityDamageEvent.DamageCause.FIRE
                || event.cause == EntityDamageEvent.DamageCause.FIRE_TICK
                || event.cause == EntityDamageEvent.DamageCause.LAVA // Change: add lava
            )
        ) {
            event.isCancelled = true
            event.entity.sendMessage("[NexusClasses] Warrior perk: Fire resistance!")
        }
    }

    // Warrior weakness: mining fatigue while holding iron weapon, slowness while wearing iron armor
    //TODO

    // Artist perk: free end pearl at all times
    //TODO: prevent dropping/moving/etc item from inventory, let them get it in the first place
    @EventHandler
    fun artistFreeEndPearl(event: PlayerItemConsumeEvent) {
        if (
            event.item.type == Material.ENDER_PEARL
            && event.item.containsEnchantment(freePearlEnchantment)
        ) {
            val pearl = ItemStack(Material.ENDER_PEARL)
            pearl.addEnchantment(freePearlEnchantment, 1)
            event.player.inventory.addItem(pearl)
            event.player.sendMessage("[NexusClasses] Artist perk: free end pearl!")
        }
    }

    // Artist weakness: damage from water
    //TODO
}