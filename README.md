## Lightweight NPC Manager Plugin

This plugin provides support for spawning NPC's, and has many customisation options, such as modifying the skin, display name, interact event and more. Also has support for using a MySQL database to store NPC data (Configured in the config.yml).

### Commands

 * `/npc create <name>`
 * `/npc modify <name> <key> <value>`
 * `/npc move <name>`
 * `/npc remove <name>`
 * `/npc info <name>`
 * `/npc list`
 * `/npc clear`
 * `/npc reload`

### NPC Modifications
An NPC can be modified by using the command:

`/npc modify <name> <key> <value>`

The accepted values for `<key>` are:

* `displayName`
* `hasHeadRotation`
* `range`
* `skin`
* `interactEvent`

### Supported Versions
This plugin currently works on spigot versions of 1.9 up to 1.16.

<i>NOTE: Support for 1.8 versions will be added soon.</i>