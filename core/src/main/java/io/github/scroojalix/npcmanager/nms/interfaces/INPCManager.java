package io.github.scroojalix.npcmanager.nms.interfaces;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.FileManager;
import io.github.scroojalix.npcmanager.utils.chat.Messages;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.sql.SQLGetter;

/**
 * The interface that contains all methods to be used in an NPCManger class
 * 
 * @author Scroojalix
 */
public abstract class INPCManager {

	protected NPCMain main;
	protected Map<String, NPCData> NPCs;

	public INPCManager(NPCMain main) {
		this.main = main;
		this.NPCs = new LinkedHashMap<String, NPCData>();
	}

	/**
	 * Returns the Hash Map containing all NPC's
	 * 
	 * @return The Hash Map which stores the NPC's in memory.
	 */
	public Map<String, NPCData> getNPCs() {
		return NPCs;
	}

	/**
	 * Creates an NPC with the Name {@code name} and Location {@code loc}
	 * 
	 * @param name The name of the NPC, used to identify it.
	 * @param loc  The location of the NPC
	 */
	public void createNPC(String name, Location loc) {
		createNPC(name, loc, true);
	}

	public void createNPC(String name, Location loc, boolean store) {
		NPCData data = new NPCData(name, loc, store);
		restoreNPC(data);
		saveNPC(data);
	}

	/**
	 * Updates an NPC with the NPCData {@code data}
	 * 
	 * @param data The NPCData assigned to an NPC.
	 */
	public void updateNPC(NPCData data) {
		removeNPC(data.getName(), false);
		restoreNPC(data);
	}

	/**
	 * Moves an NPC with Data {@code} to Location {@code loc}
	 * 
	 * @param data The NPCData assigned to an NPC
	 * @param loc  The desired location
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
	 * Deletes an NPC. Set {@code fromStorage} to true to also remove the NPC from
	 * storage.
	 * 
	 * @param npc         The name of the NPC to be deleted
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
		if (fromStorage && data.isStored()) {
			removeNPCFromStorage(data.getName());
		}
	}
	
	public void removeNPCFromStorage(String name) {
		switch (main.saveMethod) {
			case YAML:
				main.npcFile.getConfig().set("npc." + name, null);
				main.npcFile.saveConfig();
				break;
			case JSON:
				File jsonFile = new File(main.getDataFolder()+"/json-storage", name+".json");
				if (jsonFile.exists()) {
					jsonFile.delete();
				}
				break;
			case MYSQL:
				if (main.sql.getGetter().testConnection()) {
					main.sql.getGetter().remove(name);
				} else {
					FileManager temp = new FileManager(main, "temp.yml");
					if (temp.getConfig().isSet("npc."+name)) {
						temp.getConfig().set("npc."+name, null);
						temp.saveConfig();
						main.log(Level.INFO, "Removed an NPC from temp.yml");
					}
				}
				break;
		}
	}

	/**
	 * Send packets to a player which remove an NPC.
	 * 
	 * @param p    The player to send packets to.
	 * @param data The NPC to remove.
	 */
	public abstract void sendRemoveNPCPackets(Player p, NPCData data);

	/**
	 * Saves an NPC.
	 * 
	 * @param data The data to save.
	 */
	public void saveNPC(NPCData data) {
		if (data.isStored()) {
			Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable() {
				@Override
				public void run() {
					switch (main.saveMethod) {
					case YAML:
						saveYAMLNPC(data, main.npcFile);
						break;
					case JSON:
						saveJSONNPC(data);
						break;
					case MYSQL:
						SQLGetter getter = main.sql.getGetter();
						if (getter.testConnection()) {
							if (!data.isWorldNull()) {
								getter.addNPC(data);
							} else {
								main.getLogger().log(Level.WARNING,
										"Could not save NPC '" + data.getName() + "'. That world does not exist.");
							}
						} else {
							main.log(Level.WARNING, "Could not save NPC to database.");
							main.log(Level.WARNING, "Saving NPC to temp.yml instead.");
							saveYAMLNPC(data, new FileManager(main, "temp.yml"));
						}
						break;
					}
				}
			});
		}
	}

	//TODO remove YAML saving and auto update current users to use JSON instead.
	/**
	 * Saves an NPC to a .yml file.
	 * 
	 * @param data The data to save.
	 * @param file The file to save to.
	 */
	private void saveYAMLNPC(NPCData data, FileManager file) {
		file.getConfig().set("npc." + data.getName(), data.toJson(true));
		file.saveConfig();
	}

	/**
	 * Saves an NPC to a .json file.
	 * 
	 * @param data The data to save.
	 */
	private void saveJSONNPC(NPCData data) {
		File jsonFile = new File(main.getDataFolder() + "/json-storage", data.getName() + ".json");
		try {
			jsonFile.getParentFile().mkdirs();
			jsonFile.createNewFile();
		} catch (IOException e) {
			e.printStackTrace();
		}

		try {
			FileWriter writer = new FileWriter(jsonFile);
			writer.write(data.toJson(true));
			writer.close();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}

	/**
	 * Determines the right saving method, then restores NPC's using that method.
	 */
	public void restoreNPCs() {
		if (main.saveMethod != null)
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
				@Override
				public void run() {
					switch (main.saveMethod) {
						case YAML:
							if (main.npcFile.getConfig().contains("npc")) {
								main.log(Level.INFO, Messages.RESTORE_NPCS);
								restoreYAMLNPCs();
							}
							break;
						case JSON:
							if (new File(main.getDataFolder()+"/json-storage").exists()) {
								main.log(Level.INFO, Messages.RESTORE_NPCS);
								restoreJSONNPCs();
							}
							break;
						case MYSQL:
							boolean connected = main.sql.getGetter().testConnection();
							if (connected) {
								main.log(Level.INFO, Messages.RESTORE_NPCS);
								main.sql.getGetter().restoreDataEntries();
							}
							File file = new File(main.getDataFolder(), "temp.yml");
							if (file.exists()) {
								main.log(Level.INFO, "Restoring NPC's from temp.yml");
								FileManager temp = new FileManager(main, "temp.yml");
								restoreTempNPCs(temp, connected);
								if (connected) {
									file.delete();
								}
							}
							break;
					}
				}
			}, 10);
	}

	/**
	 * Restores NPC's from temp.yml
	 * 
	 * @param temp Reference to temp.yml file.
	 */
	private void restoreTempNPCs(FileManager temp, boolean connected) {
		if (temp.getConfig().contains("npc"))
			temp.getConfig().getConfigurationSection("npc").getKeys(false).forEach(current -> {
				NPCData data = NPCData.fromJson(current, temp.getConfig().getString("npc." + current), true);
				if (data != null) {
					if (connected) {
						if (!main.sql.getGetter().exists(data.getName())) {
							main.sql.getGetter().addNPC(data);
							restoreNPC(data);
						} else {
							main.log(Level.WARNING,
									"Could not merge NPC from temp.yml to database, because an NPC with the same name already exists in the database.");
						}
					} else {
						restoreNPC(data);
					}
				}
			});
	}

	/**
	 * Restores all NPC's from npcs.yml
	 */
	private void restoreYAMLNPCs() {
		if (main.npcFile.getConfig().contains("npc"))
			main.npcFile.getConfig().getConfigurationSection("npc").getKeys(false).forEach(current -> {
				NPCData data = NPCData.fromJson(current, main.npcFile.getConfig().getString("npc." + current), true);
				if (data != null)
					restoreNPC(data);
			});
	}

	private void restoreJSONNPCs() {
		File jsonStorage = new File(main.getDataFolder() + "/json-storage");
		File[] npcFiles = jsonStorage.listFiles();
		if (npcFiles != null) {
			for (int i = 0; i < npcFiles.length; i++) {
				File current = npcFiles[i];
				if (current.isFile() && current.getName().endsWith(".json")) {
					try {
						String json = new String(Files.readAllBytes(Paths.get(current.getPath())));
						NPCData data = NPCData.fromJson(current.getName().replace(".json", ""), json, true);
						if (data != null)
							restoreNPC(data);
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
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
