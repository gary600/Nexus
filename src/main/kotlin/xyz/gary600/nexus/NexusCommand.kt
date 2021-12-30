package xyz.gary600.nexus

import co.aikar.commands.BaseCommand
import co.aikar.commands.annotation.*
import org.bukkit.command.CommandSender
import org.bukkit.entity.Player
import xyz.gary600.nexus.Nexus
import xyz.gary600.nexus.nexusDebug
import xyz.gary600.nexus.nexusEnabled
import xyz.gary600.nexus.nexusMessage

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
        player.nexusDebug = enabled
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
                player.nexusMessage("Enabled Nexus for this world")
            }
            false -> {
                player.world.nexusEnabled = false
                player.nexusMessage("Disabled Nexus for this world")
            }
        }
    }

    @Subcommand("reload")
    @Description("Safely reloads Nexus' data files")
    @CommandPermission("nexus.configure")
    fun commandReload(sender: CommandSender) {
        Nexus.playerData.clear() // playerdata is lazy-loaded, so this'll reload players as needed
        Nexus.logger.info("Unloaded all playerdata")
        Nexus.loadEnabledWorlds()
        sender.nexusMessage("Reload complete: enabled Nexus in ${Nexus.enabledWorlds.size} worlds")
    }
}