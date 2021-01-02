package io.github.scroojalix.npcmanager.utils.api;

import java.util.Map;

import org.bukkit.Location;
import org.bukkit.inventory.EquipmentSlot;
import org.bukkit.inventory.ItemStack;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.chat.Messages;
import io.github.scroojalix.npcmanager.utils.interactions.CommandInteraction;
import io.github.scroojalix.npcmanager.utils.interactions.InteractEventType;
import io.github.scroojalix.npcmanager.utils.interactions.InteractionsManager;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.npc.NPCTrait;
import io.github.scroojalix.npcmanager.utils.npc.skin.NPCSkinLayers;
import io.github.scroojalix.npcmanager.utils.npc.skin.SkinLayer;
import io.github.scroojalix.npcmanager.utils.npc.skin.SkinManager;

public class NPCManagerAPI {

	/**
	 * Spawn an NPC generated from the NPCBuilder class
	 * @param data The data of the NPC to spawn.
	 */
	public static void spawnNPC(NPCData data) {
		if (!NPCMain.instance.npc.getNPCs().containsKey(data.getName())) {
			if (data.getLoc() != null) {
				data.setLoaded(true);
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
				if (PluginUtils.isAlphanumeric(name)) {
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
	 * 
	 * @param name The name of the NPC.
	 * @param slot The slot to change. 
	 * @param item The item to put in that slot.
	 */
	public static void changeEquipment(String name, EquipmentSlot slot, ItemStack item) {
		if (NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCData data = NPCMain.instance.npc.getNPCs().get(name);
			NPCTrait traits = data.getTraits();
			boolean update = true;
			switch (slot) {
			case HAND:
				if (PluginUtils.isSuitableItem(item, "mainhand", null)) {
					traits.getEquipment(true).setMainhandItem(item);
				} else { update = false; }
				break;
			case OFF_HAND:
				if (PluginUtils.isSuitableItem(item, "offhand", null)) {
					traits.getEquipment(true).setOffhandItem(item);
				} else { update = false; }
				break;
			case FEET:
				if (PluginUtils.isSuitableItem(item, "boots", null)) {
					traits.getEquipment(true).setBoots(item);
				} else { update = false; }
				break;
			case LEGS:
				if (PluginUtils.isSuitableItem(item, "leggings", null)) {
					traits.getEquipment(true).setLeggings(item);
				} else { update = false; }
				break;
			case CHEST:
				if (PluginUtils.isSuitableItem(item, "chestplate", null)) {
					traits.getEquipment(true).setChestplate(item);
				} else { update = false; }
				break;
			case HEAD:
				if (PluginUtils.isSuitableItem(item, "helmet", null)) {
					traits.getEquipment(true).setHelmet(item);
				} else { update = false; }
				break;
			default:
				update = false;
				throw new IllegalArgumentException("The equipment slot '"+slot+"' is invalid.");
			}
			if (update) {
				NPCMain.instance.npc.saveNPC(data);
				NPCMain.instance.npc.updateNPC(data);
			}
		} else {
			throw new IllegalArgumentException(Messages.UNKNOWN_NPC);
		}
	}

	public static void setDisplayName(String name, String newDisplayName) {
		if (NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCData data = NPCMain.instance.npc.getNPCs().get(name); 
			data.getTraits().setDisplayName(newDisplayName);
			NPCMain.instance.npc.saveNPC(data);
			NPCMain.instance.npc.updateNPC(data);
		} else {
			throw new IllegalArgumentException(Messages.UNKNOWN_NPC);
		}
	}
	
	public static void setSubtitle(String name, String newSubtitle) {
		if (NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCData data = NPCMain.instance.npc.getNPCs().get(name); 
			data.getTraits().setSubtitle(newSubtitle);
			NPCMain.instance.npc.saveNPC(data);
			NPCMain.instance.npc.updateNPC(data);
		} else {
			throw new IllegalArgumentException(Messages.UNKNOWN_NPC);
		}
	}
	
	public static void setHeadRotation(String name, boolean headRotation) {
		if (NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCData data = NPCMain.instance.npc.getNPCs().get(name); 
			data.getTraits().setHeadRotation(headRotation);
			NPCMain.instance.npc.saveNPC(data);
			NPCMain.instance.npc.updateNPC(data);
		} else {
			throw new IllegalArgumentException(Messages.UNKNOWN_NPC);
		}
	}

	public static void setRange(String name, int range) {
		if (NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCData data = NPCMain.instance.npc.getNPCs().get(name); 
			if (range <= 0) {
				throw new IllegalArgumentException("NPC range cannot be set to 0");
			}
			data.getTraits().setRange(range);
			NPCMain.instance.npc.saveNPC(data);
			NPCMain.instance.npc.updateNPC(data);
		} else {
			throw new IllegalArgumentException(Messages.UNKNOWN_NPC);
		}
	}
	
	public static void setSkinLayers(String name, Map<SkinLayer, Boolean> layers) {
		if (NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCData data = NPCMain.instance.npc.getNPCs().get(name); 
			if (!layers.isEmpty()) {
				NPCSkinLayers newLayers;
				if (data.getTraits().getSkinLayers() != null) {
					newLayers = data.getTraits().getSkinLayers();
				} else {
					newLayers = new NPCSkinLayers();
				}
				for (Map.Entry<SkinLayer, Boolean> layer : layers.entrySet()) {
					if (layer.getValue() != null) {
						switch (layer.getKey()) {
							case CAPE:
							newLayers.setCape(layer.getValue());
							break;
							case JACKET:
							newLayers.setJacket(layer.getValue());
							break;
							case LEFT_SLEEVE:
							newLayers.setLeftSleeve(layer.getValue());
							break;
							case RIGHT_SLEEVE:
							newLayers.setRightSleeve(layer.getValue());
							break;
							case LEFT_LEG:
							newLayers.setLeftLeg(layer.getValue());
							break;
							case RIGHT_LEG:
							newLayers.setRightLeg(layer.getValue());
							break;
							case HAT:
							newLayers.setHat(layer.getValue());
							break;
						}
					}
				}
				data.getTraits().setSkinLayers(newLayers);
				NPCMain.instance.npc.saveNPC(data);
				NPCMain.instance.npc.updateNPC(data);
			}
		} else {
			throw new IllegalArgumentException(Messages.UNKNOWN_NPC);
		}
	}
	
	/**
	 * Use this method to customise the interact event of an NPC.
	 * @param name The name of the NPC to modify.
	 * @param type The type of interaction.
	 * @param interaction The command to run or the name of the custom interact event.
	 */
	public static void setInteractEvent(String name, InteractEventType type, String interaction) {
		if (NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCData data = NPCMain.instance.npc.getNPCs().get(name);
			if (type == InteractEventType.COMMAND) {
				data.setInteractEvent(new CommandInteraction(interaction));
			} else if (type == InteractEventType.CUSTOM) {
				if (InteractionsManager.getInteractEvents().containsKey(interaction)) {
					data.setInteractEvent(InteractionsManager.getInteractEvents().get(interaction));
				} else {
					throw new IllegalArgumentException("The custom interact event '"+interaction+"' does not exist.");
				}
			} else {
				throw new IllegalArgumentException("The interaction type '"+type+"' is invalid.");
			}
		} else {
			throw new IllegalArgumentException(Messages.UNKNOWN_NPC);
		}
	}

	public static void setSkin(String name, SkinType type, String value) {
		setSkin(name, type, value, false);
	}

	/**
	 * Set the skin of an NPC.
	 * @param name The name of the NPC to modify.
	 * @param type The method of getting skin data. Can be set to <code>url</code> or <code>username</code>.
	 * @param value The URL to the skin image or the username to get textures from.
	 * @param optionalArg If <code>type</code> is set to <code>"url"</code> and
	 * this is set to <code>true</code>, then the resulting skin will use the slim model.
	 * Set it to <code>false</code> to use the default model. If <code>type</code> is set
	 * to <code>"username"</code>, then set this to <code>true</code> to automatically
	 * update the skin on every reload.
	 */
	public static void setSkin(String name, SkinType type, String value, boolean optionalArg) {
		if (NPCMain.instance.npc.getNPCs().containsKey(name)) {
			NPCData data = NPCMain.instance.npc.getNPCs().get(name);
			if (type == SkinType.URL) {
				SkinManager.setSkinFromURL(null, data, value, optionalArg);
			} else if (type == SkinType.USERNAME) {
				SkinManager.setSkinFromUsername(null, data, value, optionalArg);
			}
		} else {
			throw new IllegalArgumentException(Messages.UNKNOWN_NPC);
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
}
