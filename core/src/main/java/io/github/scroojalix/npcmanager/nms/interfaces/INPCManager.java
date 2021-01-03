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
import io.github.scroojalix.npcmanager.utils.chat.Messages;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.npc.skin.SkinManager;
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

	//TODO add config option to get skin skin data based on NPC name.
	//Upon creation call SkinManager#setSkinFromUsername
	//It gets handled from there.
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
	 * Restores a specific NPC with Data {@code data}
	 * @param data The data of the NPC to be restored
	 */
	public void restoreNPC(NPCData data) {
		createAndSpawnNPC(data);
		SkinManager.updateSkin(data);
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
		Bukkit.getScheduler().runTaskAsynchronously(main, new Runnable(){
			@Override
			public void run() {
				switch (main.saveMethod) {
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
							File tempFile = new File(main.getDataFolder()+"/json-storage/temp", name+".json");
							if (tempFile.exists()) {
								tempFile.delete();
								File tempDir = new File(main.getDataFolder()+"/json-storage/temp");
								if (tempDir.list().length == 0) {
									tempDir.delete();
								}
							}
						}
						break;
				}
			}
		});
	}

	/**
	 * Send packets to a player which remove an NPC.
	 * 
	 * @param p    The player to send packets to.
	 * @param data The NPC to remove.
	 */
	public abstract void sendRemoveNPCPackets(Player p, NPCData data);

	//TODO move all save and restore methods to its own class. (DataManager.java)
	//
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
					case JSON:
						saveJSONNPC(data, "/json-storage");
						break;
					case MYSQL:
						SQLGetter getter = main.sql.getGetter();
						if (getter.testConnection()) {
							getter.addNPC(data, true);
						} else {
							main.log(Level.SEVERE, Messages.DATABASE_NOT_CONNECTED);
							main.log(Level.SEVERE, "Saving NPC to temp storage instead.");
							saveJSONNPC(data, "/json-storage/temp");
						}
						break;
					}
				}
			});
		}
	}

	/**
	 * Saves an NPC to a .json file.
	 * 
	 * @param data The data to save.
	 */
	private void saveJSONNPC(NPCData data, String dir) {
		File jsonFile = new File(main.getDataFolder() + dir, data.getName() + ".json");
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
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {
				@Override
				public void run() {
					switch (main.saveMethod) {
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
							File tempDir = new File(main.getDataFolder()+"/json-storage/temp");
							if (tempDir.exists()) {
								main.log(Level.INFO, "Restoring NPC's from temp storage.");
								restoreTempNPCs(connected);
							}
							break;
					}
				}
			}, 1l);
	}

	/**
	 * Restores NPC's from temp.yml
	 * 
	 * @param temp Reference to temp.yml file.
	 */
	private void restoreTempNPCs(boolean connected) {
		File tempStorage = new File(main.getDataFolder()+"/json-storage/temp");
		File[] tempFiles = tempStorage.listFiles();
		if (tempFiles != null) {
			for (int i = 0; i < tempFiles.length; i++) {
				File current = tempFiles[i];
				if (current.isFile() && current.getName().endsWith(".json")) {
					try {
						String json = new String(Files.readAllBytes(Paths.get(current.getPath())));
						NPCData data = NPCData.fromJson(current.getName().replace(".json", ""), json, true);
						if (data != null) {
							boolean restore = true;
							if (connected) {
								restore = main.sql.getGetter().addNPC(data, false);
								current.delete();
							}
							if (restore) {
								restoreNPC(data);
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					}
				}
			}
		}
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
	 * Converts all data in the NPCData object into NMS stuff.
	 * @param data The {@link NPCData} to create NMS data from.
	 */
	public abstract void createAndSpawnNPC(NPCData data);
	
	/**
	 * Remove a hologram for a player
	 * @param player - The player to remove the hologram from.
	 * @param hologram - The hologram to remove.
	 */
	public abstract void removeHologramForPlayer(Player player, NMSHologram hologram);
}
