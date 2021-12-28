package xyz.gary600.nexus

import org.bukkit.Material
import org.bukkit.enchantments.Enchantment
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import xyz.gary600.nexus.extension.itemNexusClass

enum class NexusClass(private val id: Byte?) {
    // Normal Minecraft character
    Mundane(null),

    Builder(1),
    Miner(2),
    Warrior(3),
    Artist(4);

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

    fun toByte(): Byte = id ?: 0.toByte()

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

        /**
         * Parse a NexusClass from a byte
         */
        fun fromByte(b: Byte): NexusClass? = when (b) {
            // Do not parse anything to Mundane
            1.toByte() -> Builder
            2.toByte() -> Miner
            3.toByte() -> Warrior
            4.toByte() -> Artist
            else -> null // includes Mundane
        }
    }
}