package xyz.gary600.nexus.classes

import kotlinx.serialization.Serializable
import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack

/**
 * The various character classes in Nexus
 */
@Serializable
enum class NexusClass {
    // Normal Minecraft character
    Mundane,

    Builder,
    Miner,
    Warrior,
    Artist;

    fun createClassItem(type: Material, name: String): ItemStack {
        val item = ItemStack(type, 1)
        val cls = this

        // Metadata
        item.itemMeta = item.itemMeta?.apply {
            // Make it pretty
            setDisplayName(name)
            lore = listOf("${cls.name} Class Item")
            isUnbreakable = true // for Miner's headlamp leather helmet
            addEnchant(Enchantment.LOYALTY, 1, true) // Dummy enchant to add item glow
            addItemFlags(
                ItemFlag.HIDE_ENCHANTS, // Hide the enchants (nobody shall know it's really Loyalty...)
                ItemFlag.HIDE_UNBREAKABLE
            )

            // Mark as class item so it works
            itemNexusClass = cls
        }

        return item
    }

    companion object {
        /**
         * Parse a NexusClass from a string
         */
        fun fromString(str: String?): NexusClass? = when (str?.lowercase()) {
            "mundane" -> Mundane
            "builder" -> Builder
            "miner" -> Miner
            "warrior" -> Warrior
            "artist" -> Artist
            else -> null
        }
    }
}