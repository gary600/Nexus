@file:Suppress("unused")

package xyz.gary600.nexusclasses

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.enchantments.Enchantment
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.persistence.PersistentDataType
import xyz.gary600.nexusclasses.extension.debugMessages
import xyz.gary600.nexusclasses.extension.isClassItem
import xyz.gary600.nexusclasses.extension.nexusClass

@CommandAlias("nexusclass|class")
class ClassCommand : BaseCommand() {
    @Subcommand("choose|select")
    @Description("Select your class")
    @Syntax("<class>")
    @CommandPermission("nexusclasses.choose")
    fun commandChoose(player: Player, nexusClass: NexusClass) {
        player.nexusClass = nexusClass
        NexusClasses.instance!!.savePlayerData()
        player.sendMessage("[NexusClasses] Your class is now ${nexusClass.name}")
    }

    @Subcommand("set")
    @Description("Set another player's class")
    @Syntax("<class> <player>")
    @CommandPermission("nexusclasses.set")
    fun commandSet(sender: CommandSender, nexusClass: NexusClass, player: Player) {
        player.nexusClass = nexusClass
        NexusClasses.instance!!.savePlayerData()
        sender.sendMessage("[NexusClasses] Your class has been set to ${nexusClass.name}")
        sender.sendMessage("[NexusClasses] Set ${player.displayName}'s class to ${nexusClass.name}")
    }

    @Subcommand("get")
    @Description("Get a player's class")
    @Syntax("[<player>]")
    fun commandGet(sender: CommandSender, @Optional player: Player?) {
        if (player == null) {
            if (sender is Player) {
                sender.sendMessage("[NexusClasses] Your class is ${sender.nexusClass}")
            }
            else {
                sender.sendMessage("[NexusClasses] Must supply a player when on console")
            }
        }
        else {
            sender.sendMessage("[NexusClasses] ${player.displayName}'s class is ${player.nexusClass}")
        }
    }

    @Subcommand("item")
    @Description("Gives the class item if it exists and you don't have it already")
    fun commandItem(player: Player) {
        when (player.nexusClass) {
            NexusClass.Builder -> giveClassItem(player, Material.STICK, "Transmute", "Builder Class Item")
            NexusClass.Artist -> giveClassItem(player, Material.ENDER_PEARL, "Planar Blink", "Artist Class Item")
            else -> {
                player.sendMessage("[NexusClasses] Class ${player.nexusClass} doesn't have a class item")
            }
        }
    }

    // Helper function to give an enchanted class item
    private fun giveClassItem(player: Player, type: Material, displayName: String, lore: String) {
        val item = ItemStack(type, 1)
        item.addUnsafeEnchantment(Enchantment.LOYALTY, 1) // Dummy enchant to add item glow
        val meta = item.itemMeta
        meta?.setDisplayName(displayName)
        meta?.lore = listOf(lore)
        meta?.addItemFlags(ItemFlag.HIDE_ENCHANTS) // Hide the enchants (nobody shall know it's really Loyalty...)
        item.itemMeta = meta
        item.isClassItem = true
        // Only give class item if player doesn't have one yet
        if (!player.inventory.containsAtLeast(item, 1)) {
            player.inventory.addItem(item)
        }
    }

    @Subcommand("debugMessages")
    @Private
    fun commandMessages(player: Player, yesno: Boolean) {
        player.debugMessages = yesno
        NexusClasses.instance!!.savePlayerData()
        if (yesno) {
            player.sendMessage("[NexusClasses] You will now receive debug messages")
        }
        else {
            player.sendMessage("[NexusClasses] You will no longer receive debug messages")
        }
    }
}