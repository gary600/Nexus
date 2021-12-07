@file:Suppress("unused")

package xyz.gary600.nexusclasses

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player

@CommandAlias("nexusclass|class")
class ClassCommand(private val plugin: NexusClasses) : BaseCommand() {
    @Subcommand("choose|select")
    @Description("Select your class")
    @Syntax("<class>")
    @CommandPermission("nexusclasses.choose")
    fun commandChoose(player: Player, _class: Class) {
        plugin.getPlayerData(player.uniqueId)._class = _class
        player.sendMessage("[NexusClasses] Set class to ${_class.name}")
    }

    @Subcommand("set")
    @Description("Set a player's class")
    @Syntax("<player> <class>")
    @CommandPermission("nexusclasses.set")
    fun commandSet(sender: CommandSender, _class: Class, player: Player) {
        plugin.getPlayerData(player.uniqueId)._class = _class
        sender.sendMessage("[NexusClasses] Your class has been set to ${_class.name}")
        sender.sendMessage("[NexusClasses] Set ${player.displayName}'s class to ${_class.name}")
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
            sender.sendMessage("[NexusClasses] Your class is ${plugin.getPlayerData(sender.uniqueId)._class}")
        }
        else {
            sender.sendMessage("[NexusClasses] ${player.displayName}'s class is ${plugin.getPlayerData(player.uniqueId)._class}")
        }
    }
}