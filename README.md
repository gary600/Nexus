# Nexus
This is a custom Bukkit/Spigot plugin written specifically for CMURPGA's Nexus Minecraft RP. It manages custom buffs and
debuffs based on the player's chosen class.

## Class command: `/nexusclass`, alias `/class`
### Subcommand `/nexusclass choose <class>`:
*Requires permission `nexus.classes.choose`*

Selects your own class

### Subcommand `/nexusclass get [<player>]`:
Gets your own class or another player's class

### Subcommand `/nexusclass item`:
Gives you the specific item (the "class item") used to activate a class's ability, if applicable (Builder, Artist, and
Miner). Class items cannot be removed from your inventory.

### Admin subcommand `/nexusclass set <player> <class>`:
*Requires permission `nexus.classes.set`*

Sets a player's class

## Misc command: `/nexus`
### Subcommand `/nexus debug <enabled>`:
Enables or disables debug messages. Very spammy, but useful for debugging

### Admin subcommand `/nexus world [<enabled>]`:
*Requires permission `nexus.configure`*

Enables or disables the plugin in the current world. Class effects only occur in enabled worlds.

### Admin subcommand `/nexus reload`:
*Requires permission `nexus.configure`*

Safely reloads player data and enabled worlds. Always prefer this over doing `/reload` when modifying Nexus files!

## Classes and implementation
Builder (`NexusClass.Builder`, effects in `effects.BuilderEffects`):
* class item: transmutation wand (stick)
* **\+** no fall damage
* **\+** transmute blocks
* **\+** jump higher
* **\-** burn in sunlight without helmet
* **\-** helmet degrades in sunlight

Miner (`NexusClass.Miner`, effects in `effects.MinerEffects`):
* class item: headlamp (leather helmet)
* **\+** free emerald from mining some ores
* **\+** night vision below y=60 when wearing headlamp
* **\+** haste II below y=60
* **\-** extra damage from zombies

Warrior (`NexusClass.Warrior`, effects in `effects.WarriorEffects`):
* **\+** all melee weapons better than stone have Fire Aspect I
* **\+** gold weapons have Strength II
* **\+** fire/lava immunity
* **\-** holding iron weapon gives mining fatigue

Artist (`NexusClass.Artist`, effects in `effects.ArtistEffects`):
* class item: blink orb (ender pearl)
* **\+** free ender pearl at all times
* **\-** take damage in water

## License
This plugin is licensed under the Apache 2.0 license.