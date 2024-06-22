# VirtualPlayers

A plugin that allows you to create virtual players for easy debugging of plugins through console or in-game.

VirtualPlayers is a plugin designed for developers and server admins to easily test and debug plugins that require multiple players. Virtual players are non-existent in-game, and are only accessible through console or chat, meaning they will be invisible on the server list, player count and in-game.

## Features
- Run commands as a virtual player through console or chat
- Send messages as a virtual player through console or chat
- Teleport a virtual player to a specific location
- Op/de-op a virtual player to test command execution
- Attack a real player as a virtual player to test combat mechanics

## Commands
| Command                               | Description                   |
|---------------------------------------|-------------------------------|
| /vp create <name>                     | Creates a new virtual player. |
| /vp remove <name>                     | Removes a virtual player.     |
| /vp removeall                         | Removes all virtual players.  |
| /vp select <player>                   | Select a virtual player.      |
| /vp observe <player>                  | Observes a virtual player.    |
| /vp unobserve <player>                | Unobserves a virtual player.  |
| /vp list                              | Lists virtual players.        |
| /vp verbose [player] [true\|false]    | Toggles verbosity for the virtual player.                          |
| /vp teleport [player] [location]      | Teleports the virtual player to a location.                          |
| /vp command [player] <command...>     | Runs a command as a virtual player.                          |
| /vp chat [player] <message...>        | Sends a chat message as a virtual player.                          |
| /vp op [player]                       | Ops the virtual player.                          |
| /vp deop [player]                     | De-ops the virtual player.                          |
| /vp attack [player] <target> [damage] | Attacks another player as this virtual player.                          |

## Permissions
| Permission                    | Command    |
|-------------------------------|------------|
| virtualplayers.command.help   | /vp [help] |
| virtualplayers.command.create | /vp create |
| virtualplayers.command.remove | /vp remove |
| virtualplayers.command.removeall | /vp removeall |
| virtualplayers.command.select | /vp select |
| virtualplayers.command.observe | /vp observe |
| virtualplayers.command.unobserve | /vp unobserve |
| virtualplayers.command.list | /vp list |
| virtualplayers.command.verbose | /vp verbose |
| virtualplayers.command.teleport | /vp teleport |
| virtualplayers.command.command | /vp command |
| virtualplayers.command.chat | /vp chat |
| virtualplayers.command.op | /vp op |
| virtualplayers.command.deop | /vp deop |
| virtualplayers.command.attack | /vp attack |

## Links
- Website: [https://www.battleplugins.org](https://www.battleplugins.org)
- Download: [https://modrinth.com/plugin/virtualplayers](https://modrinth.com/plugin/virtualplayers)
- Discord: [BattlePlugins Discord](https://discord.com/invite/J3Hjjb8)
