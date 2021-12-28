package xyz.gary600.nexus

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.gary600.nexus.extension.debugMessages
import xyz.gary600.nexus.extension.nexusEnabled
import xyz.gary600.nexus.extension.nexusMessage

/**
 * Command for miscellaneous functions
 */
@Suppress("unused")
@CommandAlias("nexus")
@Description("Miscellaneous Nexus commands")
object NexusCommand : BaseCommand() {
    @Subcommand("debug")
    @Syntax("<enabled>")
    fun commandDebug(player: Player, enabled: Boolean) {
        player.debugMessages = enabled
        Nexus.saveData()
        if (enabled) {
            player.nexusMessage("You will now receive debug messages")
        }
        else {
            player.nexusMessage("You will no longer receive debug messages")
        }
    }

    @Subcommand("world")
    @Description("Enables/disables class effects in the current world, or gets whether it's enabled or not")
    @Syntax("[<enabled>]")
    @CommandPermission("nexus.configure")
    fun commandWorld(player: Player, @Optional enabled: Boolean?) {
        when (enabled) {
            null -> {
                if (player.world.nexusEnabled) {
                    player.nexusMessage("Nexus is enabled in this world")
                }
                else {
                    player.nexusMessage("Nexus is disabled in this world")
                }
            }
            true -> {
                player.world.nexusEnabled = true
                Nexus.saveData()
                player.nexusMessage("Enabled Nexus for this world")
            }
            false -> {
                player.world.nexusEnabled = false
                Nexus.saveData()
                player.nexusMessage("Disabled Nexus for this world")
            }
        }
    }

    @Subcommand("reload")
    @Description("Reloads the Nexus config files safely")
    @CommandPermission("nexus.configure")
    fun commandReload(sender: CommandSender) {
        // Clear loaded config data
        Nexus.playerData.clear()
        Nexus.enabledWorlds.clear()
        // Reload config data
        Nexus.loadData()
        sender.nexusMessage(
            "Reload complete:" +
                " loaded playerdata for ${Nexus.playerData.size} players" +
                " and enabled in ${Nexus.enabledWorlds.size} worlds"
        )
    }
}