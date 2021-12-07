package xyz.gary600.nexusclasses

import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.inventory.ItemStack

/**
 * A fake enchantment applied to the free pearl owned by Artists to prevent it from stacking.
 */
class FreePearlEnchantment(i: NamespacedKey) : Enchantment(i) {
    override fun getName(): String = "Artist Free Pearl"

    override fun getMaxLevel(): Int = 1

    override fun getStartLevel(): Int = 1

    override fun getItemTarget(): EnchantmentTarget = EnchantmentTarget.FISHING_ROD // dummy target cause it can't be null

    override fun isTreasure(): Boolean = false

    override fun isCursed(): Boolean = false

    override fun conflictsWith(other: Enchantment): Boolean = true

    override fun canEnchantItem(item: ItemStack): Boolean = false // don't allow manually enchanting
}