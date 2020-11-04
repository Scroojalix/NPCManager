package me.scroojalix.npcmanager.nms.interfaces;

import java.io.File;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.scroojalix.npcmanager.NPCMain;
import me.scroojalix.npcmanager.api.InteractionsManager;
import me.scroojalix.npcmanager.utils.FileManager;
import me.scroojalix.npcmanager.utils.NPCData;
import me.scroojalix.npcmanager.utils.sql.SQLGetter;
import net.md_5.bungee.api.ChatColor;

/** 
 * The interface that contains all methods to be used in an NPCManger class
 * @author Scroojalix
 */
public abstract class INPCManager {
	
	protected NPCMain main;
	protected Map<String, NPCData> NPCs;
	
	/**
	 * Returns the Hash Map containing all NPC's
	 * @return The Hash Map which stores the NPC's in memory.
	 */
	public Map<String, NPCData> getNPCs() {
		return NPCs;
	}
	
	/**
	 * Creates an NPC with the Name {@code name} and Location {@code loc}
	 * @param name The name of the NPC, used to identify it.
	 * @param loc The location of the NPC
	 */
	public void createNPC(String name, Location loc) {
		createNPC(name, loc, true);
	}

	public void createNPC(String name, Location loc, boolean store) {
		String displayName = ChatColor.stripColor(main.format(name)).isEmpty()?"Default":name;
		NPCData data = new NPCData(name, displayName, loc, store);
		restoreNPC(data);
		saveNPC(data);
	}
	
	/**
	 * Updates an NPC with the NPCData {@code data}
	 * @param data The NPCData assigned to an NPC.
	 */
	public void updateNPC(NPCData data) {
		removeNPC(data.getName(), false);
		restoreNPC(data);
	}
	
	/**
	 * Moves an NPC with Data {@code} to Location {@code loc}
	 * @param data The NPCData assigned to an NPC
	 * @param loc The desired location
	 */
	public void moveNPC(NPCData data, Location loc) {
		data.setLoc(loc);
		saveNPC(data);
		updateNPC(data);
	}
	
	/**
	 * Removes all NPCs.
	 */
	public void removeAllNPCs() {
		for (NPCData data : NPCs.values()) {
			removeNPC(data.getName(), false);
		}
		NPCs.clear();
	}

	/**
	 * Deletes an NPC. Set {@code fromStorage} to true to also remove the NPC from storage.
	 * @param npc The name of the NPC to be deleted
	 * @param fromStorage Whether or not to remove the NPC from storage.
	 */
	public void removeNPC(String npc, boolean fromStorage) {
		NPCData data = NPCs.get(npc);
		Bukkit.getScheduler().cancelTask(data.getLoaderTask());
		for (Player p : Bukkit.getOnlinePlayers()) {
			sendRemoveNPCPackets(p, data);
			if (data.getNameHolo() != null) {
				removeHologramForPlayer(p, data.getNameHolo());
			}
			if (data.getSubtitleHolo() != null) {
				removeHologramForPlayer(p, data.getSubtitleHolo());
			}
		}
		if (fromStorage) {
			switch(main.saveMethod) {
			case YAML:
				main.npcFile.getConfig().set("npc."+data.getName(), null);
				main.npcFile.saveConfig();
				break;
			case MYSQL:
				if (main.sql.getGetter().testConnection()) {
					main.sql.getGetter().remove(data.getName());
				} else {
					main.log(Level.WARNING, "Could not remove an NPC from the database, because it is offline.");
				}
				break;
			}
		}
	}

	/**
	 * Send packets to a player which remove an NPC.
	 * @param p The player to send packets to.
	 * @param data The NPC to remove.
	 */
	public abstract void sendRemoveNPCPackets(Player p, NPCData data);

	/**
	 * Saves an NPC.
	 * @param data The data to save.
	 */
	public void saveNPC(NPCData data) {
		if (data.isStored()) {
			main.log(Level.INFO, "Saving an NPC.");
			switch(main.saveMethod) {
			case YAML:
				saveYAMLNPC(data, main.npcFile);
				break;
			case MYSQL:
				SQLGetter getter = main.sql.getGetter();
				if (getter.testConnection()) {
					if (!data.isWorldNull()) {
						getter.addNPC(data);
					} else {
						main.log(Level.WARNING, "Could not save NPC '"+data.getName()+"'. That world does not exist.");
					}
				} else {
					main.log(Level.SEVERE, "Could not save NPC to database.");
					main.log(Level.SEVERE, "Saving NPC to temp.yml instead.");
					saveYAMLNPC(data, new FileManager(main, "temp.yml"));
				}
				break;
			}
		}
	}
	
	/**
	 * Saves all NPC's to a .yml file.
	 * @param file The file to save to.
	 */
	private void saveYAMLNPC(NPCData data, FileManager file) {
		if (data.getLoc() != null) {
			file.getConfig().set("npc."+data.getName(), data.toJson());
		} else {
			main.log(Level.WARNING, "Could not save '"+data.getName()+"' NPC: Unknown world");
		}
		file.saveConfig();
	}
	
	/**
	 * Determines the right saving method, then restores NPC's using that method.
	 */
	public void restoreNPCs() {
		Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			@Override
			public void run() {
				switch(main.saveMethod) {
				case YAML:
					if (main.npcFile.getConfig().contains("npc")) {
						main.log(Level.INFO, "Restoring NPCs...");
						restoreYAMLNPCs();
						main.log(Level.INFO, "Done");
					}
					break;
				case MYSQL:
					boolean connected = main.sql.getGetter().testConnection();
					if (connected) {
						main.log(Level.INFO, "Restoring NPCs...");
						main.sql.getGetter().restoreDataEntries();
						main.log(Level.INFO, "Done");
					}
					File file = new File(main.getDataFolder(), "temp.yml");
					if (file.exists()) {
						main.log(Level.INFO, "Restoring NPC's from temp.yml");
						FileManager temp = new FileManager(main, "temp.yml");
						restoreTempNPCs(temp, connected);
						if (connected) {
							file.delete();
						}
						main.log(Level.INFO, "Done");
					}
					break;
				}
			}
		}, 10);
	}

	/**
	 * Restores NPC's from temp.yml
	 * @param temp Reference to temp.yml file.
	 */
	private void restoreTempNPCs(FileManager temp, boolean connected) {
		if (temp.getConfig().contains("npc"))
		temp.getConfig().getConfigurationSection("npc").getKeys(false).forEach(current -> {
			NPCData data = NPCData.fromJson(temp.getConfig().getString("npc."+current));
			if (suitableData(data)) {
				if (connected) {
					if (!main.sql.getGetter().exists(data.getName())) {
						main.sql.getGetter().addNPC(data);
						restoreNPC(data);
					} else {
						main.log(Level.WARNING, "Could not merge NPC from temp.yml to database, because an NPC with the same name already exists in the database.");
					}
				} else {
					restoreNPC(data);
				}
			}
		});
	}

	private boolean suitableData(NPCData data) {
		if (data != null) {
			if (!data.isWorldNull()) {
				if (data.getTraits().getInteractEvent() != null) {
					String interactEvent = data.getTraits().getInteractEvent();
					if (InteractionsManager.getInteractEvents().containsKey(interactEvent)) {
						data.setInteractEvent(InteractionsManager.getInteractEvents().get(interactEvent));
					} else {
						main.log(Level.WARNING, "Error whilst restoring NPCs: Unknown interact event '"+interactEvent+"'");
						data.getTraits().setInteractEvent(null);
					}
				}
				return true;
			} else {
				main.log(Level.WARNING, "Could not reload NPC: Unknown World");
			}
		} else {
			main.log(Level.WARNING, "Could not reload NPC: Invalid JSON");
		}
		return false;
	}
	
	/**
	 * Restores all NPC's from npcs.yml
	 */
	private void restoreYAMLNPCs() {
		if (main.npcFile.getConfig().contains("npc"))
		main.npcFile.getConfig().getConfigurationSection("npc").getKeys(false).forEach(current -> {
			NPCData data = NPCData.fromJson(main.npcFile.getConfig().getString("npc."+current));
			if (suitableData(data))
				restoreNPC(data);
		});
	}
	
	/**
	 * Creates a new NMS EntityPlayer for the NPCData object.
	 * @param data The {@link NPCData} to create an NMS NPC from.
	 */
	public abstract void getNMSEntity(NPCData data);
	
	/**
	 * Restores a specific NPC with Data {@code data}
	 * @param data The data of the NPC to be restored
	 */
	public abstract void restoreNPC(NPCData data);
	
	/**
	 * Remove a hologram for a player
	 * @param player - The player to remove the hologram from.
	 * @param hologram - The hologram to remove.
	 */
	public abstract void removeHologramForPlayer(Player player, NMSHologram hologram);
}
