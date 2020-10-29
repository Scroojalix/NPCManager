package me.scroojalix.npcmanager.api;

import org.bukkit.Location;

import me.scroojalix.npcmanager.NPCMain;
import me.scroojalix.npcmanager.utils.NPCData;

public class NPC {

	public static void createNPC(String name, Location loc) {
		if (!NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCMain.instance.npc.createNPC(name, loc);
		} else {
			NPCMain.instance.getLogger().warning("Could not create NPC '"+name+"'. An NPC with that name already exists.");
		}
	}
	
	public static void modifyNPC(String name, String key, String value) {
		if (NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCData data = NPCMain.instance.npc.getNPCs().get(name);
			try {
				data.getTraits().modify(data, key, value);
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			}
		} else {
			NPCMain.instance.getLogger().warning("Could not modify NPC '"+name+"'. That NPC does not exist.");
		}
	}
	
	public static void moveNPC(String name, Location newLocation) {
		if (NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCData data = NPCMain.instance.npc.getNPCs().get(name);
			NPCMain.instance.npc.moveNPC(data, newLocation);
		} else {
			NPCMain.instance.getLogger().warning("Could not move NPC '"+name+"'. That NPC does not exist.");
		}
	}
	
	public static void removeNPC(String name) {
		if (NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCMain.instance.npc.removeNPC(name);
		} else {
			NPCMain.instance.getLogger().warning("Could not remove NPC '"+name+"'. That NPC does not exist.");
		}
	}
	
	public static void removeAllNPCs() {
		for (String npc : NPCMain.instance.npc.getNPCs().keySet()) {
			NPCMain.instance.npc.removeNPC(npc);
		}
		NPCMain.instance.npc.getNPCs().clear();
	}
}
