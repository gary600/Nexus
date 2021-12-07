package xyz.gary600.nexusclasses

import org.bukkit.NamespacedKey
import org.bukkit.enchantments.Enchantment
import org.bukkit.enchantments.EnchantmentTarget
import org.bukkit.inventory.ItemStack

/**
 * A fake enchantment applied to class items to mark them as such (and make them shiny)
 */
class ClassItemEnchantment(key: NamespacedKey) : Enchantment(key) {
    override fun getName(): String = "Nexus Class Item"

    override fun getMaxLevel(): Int = 1

    override fun getStartLevel(): Int = 1

    override fun getItemTarget(): EnchantmentTarget = EnchantmentTarget.FISHING_ROD // dummy target

    override fun isTreasure(): Boolean = false

    override fun isCursed(): Boolean = false

    override fun conflictsWith(other: Enchantment): Boolean = true

    override fun canEnchantItem(item: ItemStack): Boolean = false // don't allow manually enchanting
}