package me.scroojalix.npcmanager.utils;

import me.scroojalix.npcmanager.NPCMain;
import me.scroojalix.npcmanager.commands.NPCCommands;
import me.scroojalix.npcmanager.events.NPCEvents;

public class Initialise {
	
	public static void initialise(NPCMain main) {
		NPCMain.instance = main;
		main.saveDefaultConfig();
		main.showDebugMessages = main.getConfig().getBoolean("show-debug-messages");
		main.setSaveMethod();
		main.skinFile = new FileManager(main, "skins.yml");
		main.skinManager = new SkinManager(main);
		main.getCommand("npc").setExecutor(new NPCCommands(main));
		main.getServer().getPluginManager().registerEvents(new NPCEvents(main), main);
	}	
}
