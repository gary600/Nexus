package xyz.gary600.nexusclasses

import co.aikar.commands.BukkitCommandExecutionContext
import co.aikar.commands.InvalidCommandArgument
import co.aikar.commands.MessageKeys
import co.aikar.commands.contexts.ContextResolver

enum class Class {
    // Normal Minecraft character
    Mundane,

    Builder,
    Miner,
    Warrior,
    Artist;

    companion object {
        // Parse from text in command
        @Suppress("unused")
        fun getContextResolver(): ContextResolver<Class, BukkitCommandExecutionContext> {
            return ContextResolver { c ->
                when (c.popFirstArg().lowercase()) {
                    "mundane" -> Mundane
                    "builder" -> Builder
                    "miner" -> Miner
                    "warrior" -> Warrior
                    "artist" -> Artist
                    else -> throw InvalidCommandArgument(MessageKeys.INVALID_SYNTAX)
                }
            }
        }
    }
}