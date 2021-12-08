@file:Suppress("unused")

package xyz.gary600.nexusclasses

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import org.bukkit.inventory.ItemStack

@CommandAlias("nexusclass|class")
class ClassCommand(private val plugin: NexusClasses, private val classItemEnchantment: ClassItemEnchantment) : BaseCommand() {
    @Subcommand("choose|select")
    @Description("Select your class")
    @Syntax("<class>")
    @CommandPermission("nexusclasses.choose")
    fun commandChoose(player: Player, nexusClass: NexusClass) {
        plugin.getPlayerData(player.uniqueId).nexusClass = nexusClass
        plugin.savePlayerData()
        player.sendMessage("[NexusClasses] Your class is now ${nexusClass.name}")
    }

    @Subcommand("set")
    @Description("Set another player's class")
    @Syntax("<class> <player>")
    @CommandPermission("nexusclasses.set")
    fun commandSet(sender: CommandSender, nexusClass: NexusClass, player: Player) {
        plugin.getPlayerData(player.uniqueId).nexusClass = nexusClass
        plugin.savePlayerData()
        sender.sendMessage("[NexusClasses] Your class has been set to ${nexusClass.name}")
        sender.sendMessage("[NexusClasses] Set ${player.displayName}'s class to ${nexusClass.name}")
    }

    @Subcommand("get")
    @Description("Get a player's class")
    @Syntax("[<player>]")
    fun commandGet(sender: CommandSender, @Optional player: Player?) {
        if (player == null) {
            if (sender !is Player) {
                sender.sendMessage("[NexusClasses] Must supply a player when on console")
                return
            }
            sender.sendMessage("[NexusClasses] Your class is ${plugin.getPlayerData(sender.uniqueId).nexusClass}")
        }
        else {
            sender.sendMessage("[NexusClasses] ${player.displayName}'s class is ${plugin.getPlayerData(player.uniqueId).nexusClass}")
        }
    }

    @Subcommand("item")
    @Description("Gives the class item if it exists and you don't have it already")
    fun commandItem(player: Player) {
        when (val nexusClass = plugin.getPlayerData(player.uniqueId).nexusClass) {
            NexusClass.Builder -> giveClassItem(player, Material.STICK, "[Builder] Transmute")
            NexusClass.Artist -> giveClassItem(player, Material.ENDER_PEARL, "[Artist] Planar Blink")
            else -> {
                player.sendMessage("[NexusClasses] Class $nexusClass doesn't have a class item")
            }
        }
    }

    // Helper function to give an enchanted class item
    private fun giveClassItem(player: Player, type: Material, displayName: String) {
        val item = ItemStack(type, 1)
        item.addUnsafeEnchantment(classItemEnchantment, 1)
        val meta = item.itemMeta
        meta?.setDisplayName(displayName)
        item.itemMeta = meta
        // Only give class item if player doesn't have one yet
        if (!player.inventory.containsAtLeast(item, 1)) {
            player.inventory.addItem(item)
        }
    }

    @Subcommand("debugMessages")
    @Private
    fun commandMessages(player: Player, yesno: Boolean) {
        plugin.getPlayerData(player.uniqueId).debugMessages = yesno
        plugin.savePlayerData()
        if (yesno) {
            player.sendMessage("[NexusClasses] You will now receive debug messages")
        }
        else {
            player.sendMessage("[NexusClasses] You will no longer receive debug messages")
        }
    }
}