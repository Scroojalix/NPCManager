package io.github.scroojalix.npcmanager.nms.interfaces;

import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.npc.skin.SkinManager;

/**
 * The interface that contains all methods to be used in an NPCManger class
 * 
 * @author Scroojalix
 */
public abstract class INPCManager {

	protected NPCMain main;
	protected Map<String, NPCData> NPCs;
	private boolean fetchDefaultSkins;
	private int npcNameLength;

	public INPCManager(NPCMain main) {
		this.main = main;
		this.NPCs = new LinkedHashMap<String, NPCData>();
		fetchDefaultSkins = main.getConfig().getBoolean("fetch-default-skins");
		npcNameLength = main.getConfig().getInt("npc-name-length");
		if (npcNameLength > 16) npcNameLength = 16;
		if (npcNameLength < 3) npcNameLength = 3;
		main.log(Level.INFO, "Set NPC tab list name length to "+npcNameLength);
	}

	public void setNPCNameLength(int npcNameLength) {
		this.npcNameLength = npcNameLength;
	}

	/**
	 * Returns the Hash Map containing all NPC's
	 * 
	 * @return The Hash Map which stores the NPC's in memory.
	 */
	public Map<String, NPCData> getNPCs() {
		return NPCs;
	}

	public void createNPC(String name, Location loc, boolean store) {
		NPCData data = new NPCData(name, loc, store);
		main.storage.saveNPC(data);
		spawnNPC(data);
		if (fetchDefaultSkins) {
			SkinManager.setSkinFromUsername(null, data, name, false, true);
		}
	}

	/**
	 * Updates an NPC with the NPCData {@code data}
	 * 
	 * @param data The NPCData assigned to an NPC.
	 */
	public void updateNPC(NPCData data) {
		removeNPC(data.getName(), false);
		spawnNPC(data);
	}

	/**
	 * Restores a specific NPC with Data {@code data}
	 * @param data The data of the NPC to be restored
	 */
	public void spawnNPC(NPCData data) {
		createNPCData(data);
		NPCs.put(data.getName(), data);
		startLoaderTask(data);
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
		main.storage.saveNPC(data);
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
		Bukkit.getScheduler().cancelTask(data.getLoaderTaskID());
		data.getLoaderTask().clearAllTasks();
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
			main.storage.removeNPC(data.getName());
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
	 * Converts all data in the NPCData object into NMS stuff.
	 * @param data The {@link NPCData} to create NMS data from.
	 */
	public abstract void createNPCData(NPCData data);

	/**
	 * Spawns an NPC, assuming all the NMS code has been generated.
	 * @param data The NPC to spawn.
	 */
	public abstract void startLoaderTask(NPCData data);
	
	/**
	 * Remove a hologram for a player
	 * @param player - The player to remove the hologram from.
	 * @param hologram - The hologram to remove.
	 */
	public abstract void removeHologramForPlayer(Player player, NMSHologram hologram);

	public String getRandomNPCName() {
		char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_".toCharArray();
		SecureRandom rand = new SecureRandom();
		char[] result = new char[npcNameLength];
		for (int i = 0; i < result.length; i++) {
			result[i] = chars[rand.nextInt(chars.length)];
		}
		return new String(result);
	}
}
