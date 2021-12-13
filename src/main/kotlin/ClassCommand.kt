package xyz.gary600.nexusclasses

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import co.aikar.commands.bukkit.contexts.OnlinePlayer
import org.bukkit.Material
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.gary600.nexusclasses.extension.debugMessages
import xyz.gary600.nexusclasses.extension.nexusClass
import xyz.gary600.nexusclasses.extension.nexusClassesEnabled
import xyz.gary600.nexusclasses.extension.nexusMessage

@Suppress("unused")
@CommandAlias("nexusclass|class")
class ClassCommand : BaseCommand() {
    @Subcommand("choose|select")
    @Description("Select your class")
    @Syntax("<class>")
    @CommandPermission("nexusclasses.choose")
    fun commandChoose(player: Player, nexusClass: NexusClass) {
        player.nexusClass = nexusClass
        NexusClasses.instance!!.saveData()
        player.nexusMessage("Your class is now ${nexusClass.name}")
    }

    @Subcommand("set")
    @Description("Set another player's class")
    @Syntax("<class> <player>")
    @CommandPermission("nexusclasses.set")
    fun commandSet(sender: CommandSender, player: OnlinePlayer, nexusClass: NexusClass) {
        player.player.nexusClass = nexusClass
        NexusClasses.instance!!.saveData()
        player.player.nexusMessage("Your class has been set to ${nexusClass.name}")
        sender.nexusMessage("Set ${player.player.displayName}'s class to ${nexusClass.name}")
    }

    @Default
    @Subcommand("get")
    @Description("Get a player's class")
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
        if (player.world.nexusClassesEnabled) {
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
            player.nexusMessage("Class items can only be obtained in worlds where NexusClasses is enabled")
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

    @Subcommand("world")
    @Description("Enables/disables class effects in the current world, or gets whether it's enabled or not")
    @Syntax("[<enabled>]")
    @CommandPermission("nexusclasses.configure")
    fun commandWorld(player: Player, @Optional enabled: Boolean?) {
        when (enabled) {
            null -> {
                if (player.world.nexusClassesEnabled) {
                    player.nexusMessage("Class effects are enabled in this world")
                }
                else {
                    player.nexusMessage("Class effects are disabled in this world")
                }
            }
            true -> {
                player.world.nexusClassesEnabled = true
                NexusClasses.instance!!.saveData()
                player.nexusMessage("Class effects enabled for this world")
            }
            false -> {
                player.world.nexusClassesEnabled = false
                NexusClasses.instance!!.saveData()
                player.nexusMessage("Class effects disabled for this world")
            }
        }
    }

    @Subcommand("debugMessages")
    @Private
    fun commandMessages(player: Player, yesno: Boolean) {
        player.debugMessages = yesno
        NexusClasses.instance!!.saveData()
        if (yesno) {
            player.nexusMessage("You will now receive debug messages")
        }
        else {
            player.nexusMessage("You will no longer receive debug messages")
        }
    }
}