package xyz.gary600.nexus.classes

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.gary600.nexus.nexusEnabled
import xyz.gary600.nexus.nexusMessage

@Suppress("unused")
@CommandAlias("nexusclass|class")
@Description("Manage your Nexus class")
object ClassCommand : BaseCommand() {
    @Subcommand("choose|select")
    @Description("Select your class")
    @Syntax("<class>")
    @CommandPermission("nexus.classes.choose")
    fun commandChoose(player: Player, nexusClass: NexusClass) {
        player.nexusClass = nexusClass
        player.nexusMessage("Your class is now ${nexusClass.name}")
    }

    @Subcommand("set")
    @Description("Set another player's class")
    @Syntax("<class> <player>")
    @CommandPermission("nexus.classes.set")
    fun commandSet(sender: CommandSender, player: OnlinePlayer, nexusClass: NexusClass) {
        player.player.nexusClass = nexusClass
        player.player.nexusMessage("Your class has been set to ${nexusClass.name}")
        sender.nexusMessage("Set ${player.player.displayName}'s class to ${nexusClass.name}")
    }

    @Subcommand("get")
    @Description("Get a player's (or your own) class")
    @Syntax("[<player>]")
    fun commandGet(sender: CommandSender, @Optional player: OnlinePlayer?) {
        if (player == null) {
            if (sender is Player) {
                sender.nexusMessage("Your class is ${sender.nexusClass}")
            }
            else {
                sender.nexusMessage("Must supply a player when on console")
            }
        }
        else {
            sender.nexusMessage("${player.player.displayName}'s class is ${player.player.nexusClass}")
        }
    }

    @Subcommand("item")
    @Description("Gives the class item if it exists and you don't have it already")
    fun commandItem(player: Player) {
        if (player.world.nexusEnabled) {
            when (player.nexusClass) {
                NexusClass.Builder -> giveClassItem(player, Material.STICK, "Transmute")
                NexusClass.Miner -> giveClassItem(player, Material.LEATHER_HELMET, "Headlamp")
                NexusClass.Artist -> giveClassItem(player, Material.ENDER_PEARL, "Planar Blink")
                else -> {
                    player.nexusMessage("Class ${player.nexusClass} doesn't have a class item")
                }
            }
        }
        else {
            player.nexusMessage("Class items can only be obtained in worlds where Nexus is enabled")
        }
    }

    // Helper function to give an enchanted class item
    private fun giveClassItem(player: Player, type: Material, name: String) {
        val item = player.nexusClass.createClassItem(type, name)

        // Only give class item if player doesn't have one yet
        if (!player.inventory.containsAtLeast(item, 1)) {
            player.inventory.addItem(item)
        }
    }
}