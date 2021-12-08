package xyz.gary600.nexusclasses

import co.aikar.commands.BukkitCommandExecutionContext
import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.MessageKeys
import co.aikar.commands.contexts.ContextResolver

enum class NexusClass {
    // Normal Minecraft character
    Mundane,

    Builder,
    Miner,
    Warrior,
    Artist;

    companion object {
        // Parse from text
        fun parse(str: String?): NexusClass? = when (str?.lowercase()) {
            "mundane" -> Mundane
            "builder" -> Builder
            "miner" -> Miner
            "warrior" -> Warrior
            "artist" -> Artist
            else -> null
        }

        // Parse from text in command
        @Suppress("unused")
        fun getContextResolver(): ContextResolver<NexusClass, BukkitCommandExecutionContext> = ContextResolver { c ->
            parse(c.popFirstArg()) ?: throw InvalidCommandArgument(MessageKeys.INVALID_SYNTAX)
        }
    }
}