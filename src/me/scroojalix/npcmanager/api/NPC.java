package me.scroojalix.npcmanager.api;

import org.bukkit.ChatColor;
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
	
	public static void setNPCSkin(String name, String skin) {
		if (NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCData data = NPCMain.instance.npc.getNPCs().get(name);
			if (NPCMain.instance.skinManager.values().contains(skin)) {
				NPCMain.instance.npc.setSkin(data, skin);
			} else {
				NPCMain.instance.getLogger().warning("Could not set the skin of '"+name+"'. That skin does not exist.");
			}
		} else {
			NPCMain.instance.getLogger().warning("Could not set the skin of '"+name+"'. That NPC does not exist.");
		}
	}
	
	public static void modifyNPC(String name, String key, String value) {
		if (NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCData data = NPCMain.instance.npc.getNPCs().get(name);
			switch(key) {
			case "displayName":
				data.setDisplayName(ChatColor.stripColor(NPCMain.instance.format(value)).isEmpty()?null:value);
				NPCMain.instance.npc.updateNPC(data);
				break;
			case "hasHeadRotation":
				data.setHasHeadRotation(value.equalsIgnoreCase("true"));
				NPCMain.instance.npc.updateNPC(data);
				break;
			case "range":
				try {
					Integer range = Integer.parseInt(value);
					if (range <= 0) {
						NPCMain.instance.getLogger().warning("Could not change range of '"+name+"'. Range cannot be set to 0 or below.");
						return;
					}
					data.setRange(range);
					NPCMain.instance.npc.updateNPC(data);
				} catch(Exception e) {
					NPCMain.instance.getLogger().warning("Could not change range of '"+name+"'. '"+value+"' is not a number.");
				}
				break;
			case "skin":
				setNPCSkin(name, value);
				break;
			case "interactEvent":
				if (!value.equalsIgnoreCase("None")) {
					if (InteractionsManager.getInteractEvents().containsKey(value)) {
						data.setInteractEvent(InteractionsManager.getInteractEvents().get(value));
						NPCMain.instance.npc.updateNPC(data);
					} else {
						NPCMain.instance.getLogger().warning("Could not change Interact Event of '"+name+"'. '"+value+"' is not a valid Interact Event.");
					}
				} else {
					data.setInteractEvent(null);
					NPCMain.instance.npc.updateNPC(data);
				}
				break;
			default:
				NPCMain.instance.getLogger().warning("Could not modify NPC '"+name+"'. Unknown key '"+key+"'.");
				break;
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
