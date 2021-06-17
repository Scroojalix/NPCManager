## Lightweight NPC Manager Plugin

This plugin provides support for spawning NPC's, and has many customisation options, such as modifying the skin, display name, interact event and more. 

Also has support for using a MySQL database to store NPC data (Configured in the config.yml).

### Disclaimer
This project was created so I could practice coding with Java, as well as learn how to use the Spigot API. This plugin may be very buggy and may not have that many features, so by all means use something more advanced such as [Citizens](https://www.spigotmc.org/resources/citizens.13811/).

This project was built and tested on a Windows computer. I have no idea if it will work on Linux/MacOS, so if you wish to try this on one of those platforms, then please feel free to post any issues in the issues tab.

### Commands

 * `/npc create <name>`
 * `/npc modify <npc> <key> <value>`
 * `/npc remove <npc>`
 * `/npc move <npc>`
 * `/npc tpto <npc>`
 * `/npc list`
 * `/npc clear`
 * `/npc info <npc>`
 * `/npc reload`
 * `/npc rename`

### NPC Modifications
An NPC can be modified by using the command:

`/npc modify <npc> <key> [args...]`

The accepted values for `<key>` are:

* `displayName`
* `equipment`
* `hasHeadRotation`
* `interactEvent`
* `range`
* `skin`
* `skinLayers`
* `subtitle`

### Supported Versions

This plugin supports a wide range of Spigot versions, from 1.8.3 to 1.16.5.

### How to build

It is not required to build the plugin, as I will do so upon each release. I have included this information here in the event that i forget to build and upload the plugin.

#### Requirements:

 * [Maven](https://maven.apache.org/download.cgi)
 * [Java JDK](https://www.oracle.com/au/java/technologies/javase-downloads.html)
 * [Git](https://git-scm.com/downloads)

Run the following commands in the command line:

```
git clone https://github.com/Scroojalix/NPCManager.git
```
```
cd NPCManager
```
```
mvn clean install
```

The .jar file can be found in the `Build` folder.

