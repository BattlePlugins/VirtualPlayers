VirtualPlayers2
======
Bukkit plugin that creates virtual players: 
Used for easy debugging of plugins through the console.


VirtualPlayers2 was made for developers who need to easily test 
their plugins against multiple versions of craftbukkit.


Downloads:
---

**Official builds**

You can find the official builds at dev.bukkit.org

[https://dev.bukkit.org/projects/virtualplayers2](http://dev.bukkit.org/bukkit-plugins/virtualplayers2/ "Official builds")


**Development builds**

```python
"Development builds of this project can be acquired at the provided maven repository."
"These builds have not been approved by the BukkitDev staff. Use them at your own risk."
```

[https://ci.battleplugins.org/job/BattlePlugins/job/VirtualPlayers/](https://ci.battleplugins.org/job/BattlePlugins/job/VirtualPlayers/ "Dev builds")

The dev builds are primarily for testing purposes.


Commands as Players
---

|command|description|
|:------|:----------|
|`dc <virtual player>`  | any command you would normally do as a player |
|`dc <virtual player> op` | op virtual player |
|`dc <virtual player> deop` | deop virtual player |
|`dc <virtual player> respawn` | respawn the virtual player |
|`dc <virtual player> connect` | virtual player connects to the server |
|`dc <virtual player> disconnect` | virtual player disconnects from the server |
|`dc <virtual player> reconnect` | virtual player reconnects to the server |
|`dc <virtual player> health <amount>` | give the virtual player some health ( or kill them if 0 ) |
|`dc <virtual player> inv` | print out their inventory |
|`dc <virtual player> giveinv <item>` | give them an item |
|`dc <virtual player> tp <location>` | teleport them to the given location ( "world,3,3,3" ) |
|`dc <virtual player> chat my message` | have the virtual player talk in chat |
|`dc <virtual player> hit <player2> [damage]` | attack other players, defaults to 5 damage |
|`dc <virtual player> interact <left, right> <location>` | do a PlayerInteractEvent on the specified block location |
|`/dc <virtual player> bpe <block> <location>` | do a BlockPlaceEvent of the given material at the given location |
|`/dc <virtual player> bbe <location>` | do a BlockBreakEvent at the given location |
|`/dc <virtual player> pie | Trigger a PlayerPickupItemEvent |


Other Commands
---

|command|description|
|:------|:----------|
|`/virtualplayers hideMessages` | hide messages from players |
|`/virtualplayers showMessages` | show messages from players |


Change Log
---
- **v1.6.2**
  * Updated to Minecraft 1.8 compatibility.
- **v1.6.0**
  * Added PIE command (PlayerPickupItemEvent)
  * `/dc vp pie` will trigger a PlayerPickupItemEvent.
  * The VirtualPlayer will pickup items on the ground at his/her location.
- **v1.5.10**
  * Fixed BlockPlaceEvent command.
  * Compatible with 1.2.5 to 1.7.10
- **v1.5.9**
  * Added backwards compatibility.
  * You can now use one JAR for all craftbukkit versions.
  * Tested against craftbukkit versions 1.2.5 to 1.7.9
- **v1.5.6**
  * `/dc help`: now shows help
  * `/dc <vp> sneak <true or false>` : now sneaks and unsneaks
  * Breaks the BlockPlaceEvent cmd: `/dc <vp> bpe <block> <location>`
- **v1.4.3**
  * Fixes for cross world moves
- **v1.4.1.1**
  * Command is now vdc, with an alias for dc
- **v1.4**
  * More API commands for making virtual players
- **v1.3.1**
  * Fixes for bpe and bbe
- **v1.3**
  * new command : `/dc <virtual player> bpe <block> <location>` : do a BlockPlaceEvent of the given material at the given location
  * new command : `/dc <virtual player> bbe <location>` : do a BlockBreakEvent at the given location
- **v1.2.5**
  * new command : `/dc <virtual player> interact <left|right> <location>` : interact with the location with a left or right click, with whatever item they have in hand.
- **v1.2.4**
  * new command. givehelm. Set the virtual players helm
  * Fix for creating virtual players in servers that don't have world
- **v1.2.3**
  * new command: `/dc <virtualplayer> hit <player2> [damage]` <- attack other players
  * new command: `/dc <virtualplayer> interact <location>` <- do a block click event
- **v1.2.0**
  * Add ability for virtual players to fire AsyncPlayerChat events by the use of `/dc <virtual player> chat <message>`


Contact:
======

Live Chat on Discord:
* [BattlePlugins Discord](https://discord.gg/tMVPVJf): Join our Discord server to get support, talk about dev stuff, or just say hi!

