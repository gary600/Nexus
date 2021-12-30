package xyz.gary600.nexus

//import net.md_5.bungee.api.ChatMessageType
//import net.md_5.bungee.api.chat.TextComponent
//import org.bukkit.Bukkit
import org.bukkit.event.EventHandler
import org.bukkit.event.player.PlayerQuitEvent

@Suppress("unused")
object MiscEffects : Effects() {
    // Unload playerdata when the player logs off
    @EventHandler
    fun unloadPlayerData(event: PlayerQuitEvent) {
        Nexus.playerData.remove(event.player.uniqueId)

        Nexus.logger.info("Unloaded playerdata for player ${event.player.displayName}")
    }

    // Alert players who are in-character that they are
//    @TimerTask(0, 30) // Fades out after 1.5 seconds, need to refresh
//    fun rpActionBar() {
//        Bukkit.getServer().onlinePlayers.filter { player ->
//            player.world.nexusEnabled
//                    // && player.chatChannel ==
//        }.forEach { player ->
//            player.spigot().sendMessage(ChatMessageType.ACTION_BAR, TextComponent("Chatting in-chracter"))
//        }
//    }
}