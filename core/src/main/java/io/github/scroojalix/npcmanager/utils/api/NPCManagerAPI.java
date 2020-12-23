package io.github.scroojalix.npcmanager.utils.api;

import org.bukkit.Location;
import org.bukkit.inventory.ItemStack;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.chat.Messages;
import io.github.scroojalix.npcmanager.utils.interactions.CommandInteraction;
import io.github.scroojalix.npcmanager.utils.interactions.InteractionsManager;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.npc.NPCEquipment;
import io.github.scroojalix.npcmanager.utils.npc.NPCTrait;

public class NPCManagerAPI {

	/**
	 * Spawn an NPC generated from the NPCBuilder class
	 * @param data The data of the NPC to spawn.
	 */
	public static void spawnNPC(NPCData data) {
		if (!NPCMain.instance.npc.getNPCs().containsKey(data.getName())) {
			if (data.getLoc() != null) {
				NPCMain.instance.npc.saveNPC(data);
				NPCMain.instance.npc.restoreNPC(data);
			} else {
				throw new NullPointerException("The NPC's location is null (not valid).");
			}
		} else {
			throw new IllegalArgumentException("An NPC with that name already exists.");
		}
	}

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
		if (!PluginUtils.npcExists(name)) {
			if (name.length() <= 16) {
				if (PluginUtils.isAlphaNumeric(name)) {
					NPCMain.instance.npc.createNPC(name, loc, store);
				} else {
					throw new IllegalArgumentException(Messages.NOT_ALPHANUMERIC);
				}
			} else {
				throw new IllegalArgumentException(Messages.LONG_NAME);
			}
		} else {
			throw new IllegalArgumentException("An NPC with the name '"+name+"' already exists.");
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
				if (PluginUtils.isSuitableItem(item, "mainhand", null)) {
					equipment.setMainhandItem(item);
				} else { update = false; }
				break;
			case 1:
				if (PluginUtils.isSuitableItem(item, "offhand", null)) {
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
				throw new IllegalArgumentException("The equipment slot '"+slot+"' is invalid.");
			}
			if (update) {
				NPCMain.instance.npc.saveNPC(data);
				NPCMain.instance.npc.updateNPC(data);
			} else if (slot >= 0 && slot <= 5) {
				throw new IllegalArgumentException("That item is not suitable for that slot.");
			}
		} else {
			throw new IllegalArgumentException(Messages.UNKNOWN_NPC);
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
			modify(data, key, value);
			NPCMain.instance.npc.saveNPC(data);
			NPCMain.instance.npc.updateNPC(data);
		} else {
			throw new IllegalArgumentException(Messages.UNKNOWN_NPC);
		}
	}

	/**
	 * Use this method to customise the interact event of an NPC.
	 * @param name The name of the NPC to modify.
	 * @param type The type of interaction. Can be <code>command</code> or <code>custom</code>.
	 * @param interaction The command to run or the name of the custom interact event.
	 */
	public static void changeInteractEvent(String name, String type, String interaction) {
		if (NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCData data = NPCMain.instance.npc.getNPCs().get(name);
			if (type.equalsIgnoreCase("command")) {
				data.setInteractEvent(new CommandInteraction(interaction));
			} else if (type.equalsIgnoreCase("custom")) {
				if (InteractionsManager.getInteractEvents().containsKey(interaction)) {
					data.setInteractEvent(InteractionsManager.getInteractEvents().get(interaction));
				} else {
					throw new IllegalArgumentException("The custom interact event '"+interaction+"' does not exist.");
				}
			} else {
				throw new IllegalArgumentException("The interaction type '"+type+"' is invalid.");
			}
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
			throw new IllegalArgumentException(Messages.UNKNOWN_NPC);
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
			throw new IllegalArgumentException(Messages.UNKNOWN_NPC);
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

	private static void modify(NPCData data, String key, String value) {
		NPCTrait traits = data.getTraits();
		switch(key) {
			case "displayName":
				traits.setDisplayName(value.equalsIgnoreCase("none")?null:value);
				return;
			case "subtitle":
				traits.setSubtitle(value.equalsIgnoreCase("none")?null:value);
				return;
			case "hasHeadRotation":
				traits.setHeadRotation(value.equalsIgnoreCase("true"));
				return;
			case "range":
				try {
					Integer range = Integer.parseInt(value);
					if (range <= 0) {
						throw new IllegalArgumentException("Range cannot be set to 0");
					}
					traits.setRange(range);
				} catch(NumberFormatException e) {
					throw new IllegalArgumentException("'"+value+"' is not a number.");
				}
			case "skin":
				if (value.equalsIgnoreCase("null") || NPCMain.instance.skinManager.values().contains(value)) {
					traits.setSkin(value.equalsIgnoreCase("null")?null:value);
					return;
				} else {
					throw new IllegalArgumentException("'"+value+"' is not a valid skin.");
				}
			default:
				throw new IllegalArgumentException("Unknown key '"+key+"'.");
			}
	}
}
