package me.scroojalix.npcmanager.api;

import org.bukkit.Location;

import me.scroojalix.npcmanager.NPCMain;
import me.scroojalix.npcmanager.utils.NPCData;

public class NPC {

	/**
	 * Creates an NPC.
	 * @param name The name used to identify the NPC.
	 * @param loc The Location to spawn this NPC at.
	 */
	public static void createNPC(String name, Location loc) {
		if (!NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCMain.instance.npc.createNPC(name, loc);
		} else {
			NPCMain.instance.getLogger().warning("Could not create NPC '"+name+"'. An NPC with that name already exists.");
		}
	}
	
	/**
	 * Modifies an NPC.
	 * @param name The name
	 */
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
	
	/**
	 * Changes the location of an NPC.
	 * @param name The name of the NPC that is being moved.
	 * @param newLocation The intended Location.
	 */
	public static void moveNPC(String name, Location newLocation) {
		if (NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCData data = NPCMain.instance.npc.getNPCs().get(name);
			NPCMain.instance.npc.moveNPC(data, newLocation);
		} else {
			NPCMain.instance.getLogger().warning("Could not move NPC '"+name+"'. That NPC does not exist.");
		}
	}
	
	/**
	 * Removes an NPC.
	 * @param name The NPC to be removed.
	 */
	public static void removeNPC(String name) {
		if (NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCMain.instance.npc.removeNPC(name);
		} else {
			NPCMain.instance.getLogger().warning("Could not remove NPC '"+name+"'. That NPC does not exist.");
		}
	}
	
	/**
	 * Removes all NPC's.
	 */
	public static void removeAllNPCs() {
		for (String npc : NPCMain.instance.npc.getNPCs().keySet()) {
			NPCMain.instance.npc.removeNPC(npc);
		}
		NPCMain.instance.npc.getNPCs().clear();
	}
}
