package io.github.scroojalix.npcmanager.utils.api;

import java.util.logging.Level;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.npc.NPCEquipment;
import io.github.scroojalix.npcmanager.utils.npc.NPCTrait;

public class NPCManagerAPI {

	/**
	 * Creates an NPC. Store parameter is set to true.
	 * @param name Name of the NPC.
	 * @param loc Location of the NPC.
	 */
	public static void createNPC(String name, Location loc) {
		createNPC(name, loc, true);
	}

	/**
	 * Creates an NPC.
	 * @param name The name used to identify the NPC.
	 * @param loc The Location to spawn this NPC at.
	 * @param store Whether or not to store the NPC in a file or database.
	 */
	public static void createNPC(String name, Location loc, boolean store) {
		if (!NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCMain.instance.npc.createNPC(name, loc, store);
		} else {
			NPCMain.instance.getLogger().warning("Could not create NPC '"+name+"'. An NPC with that name already exists.");
		}
	}
	
	/**
	 * Modify an NPC's equipment.
	 * <p>
	 * For the slot parameter, use a number between 0 and 5.
	 * <p>
	 * 0 - main hand
	 * <p>
	 * 1 - off hand
	 * <p>
	 * 2 - boots
	 * <p>
	 * 3 - leggings
	 * <p>
	 * 4 - chestplate
	 * <p>
	 * 5 - helmet
	 * 
	 * @param name The name of the NPC.
	 * @param slot The slot to change. 
	 * @param item The item to put in that slot.
	 */
	public static void changeEquipment(String name, int slot, ItemStack item) {
		if (NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCData data = NPCMain.instance.npc.getNPCs().get(name);
			NPCEquipment equipment = data.getTraits().getEquipment();
			boolean update = true;
			switch (slot) {
			case 0:
				if (PluginUtils.isSuitableItem(item, "item", null)) {
					equipment.setMainhandItem(item);
				} else { update = false; }
				break;
			case 1:
				if (PluginUtils.isSuitableItem(item, "item", null)) {
					equipment.setOffhandItem(item);
				} else { update = false; }
				break;
			case 2:
				if (PluginUtils.isSuitableItem(item, "boots", null)) {
					equipment.setBoots(item);
				} else { update = false; }
				break;
			case 3:
				if (PluginUtils.isSuitableItem(item, "leggings", null)) {
					equipment.setLeggings(item);
				} else { update = false; }
				break;
			case 4:
				if (PluginUtils.isSuitableItem(item, "chestplate", null)) {
					equipment.setChestplate(item);
				} else { update = false; }
				break;
			case 5:
				if (PluginUtils.isSuitableItem(item, "helmet", null)) {
					equipment.setHelmet(item);
				} else { update = false; }
				break;
			default:
				update = false;
				NPCMain.instance.getLogger().warning("Could not modify the equipment of '"+name+"'. The slot '"+slot+"' does not exist.");
				break;
			}
			if (update) {
				NPCMain.instance.npc.saveNPC(data);
				NPCMain.instance.npc.updateNPC(data);
			} else if (slot >= 0 && slot <= 5) {
				NPCMain.instance.getLogger().warning("Could not modify the equipment of '"+name+"'. That item is not suitable for that slot.");
			}
		} else {
			NPCMain.instance.getLogger().warning("Could not modify the equipment of '"+name+"'. That NPC does not exist.");
		}
	}
	
	/**
	 * Modifies an NPC.
	 * @param name The name of the NPC to modify.
	 * @param key The {@link NPCTrait} to modify.
	 * @param value The new value of the key
	 */
	public static void modifyNPC(String name, String key, String value) {
		if (NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCData data = NPCMain.instance.npc.getNPCs().get(name);
			try {
				data.getTraits().modify(data, key, value);
			} catch (IllegalArgumentException e) {
				NPCMain.instance.log(Level.SEVERE, e.getMessage());
			} catch (Throwable t) {}
			NPCMain.instance.npc.saveNPC(data);
			NPCMain.instance.npc.updateNPC(data);
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
			NPCMain.instance.npc.removeNPC(name, true);
			NPCMain.instance.npc.getNPCs().remove(name);
		} else {
			NPCMain.instance.getLogger().warning("Could not remove NPC '"+name+"'. That NPC does not exist.");
		}
	}
	
	/**
	 * Removes all NPC's.
	 */
	public static void removeAllNPCs() {
		for (String npc : NPCMain.instance.npc.getNPCs().keySet()) {
			NPCMain.instance.npc.removeNPC(npc, true);
		}
		NPCMain.instance.npc.getNPCs().clear();
	}
}
