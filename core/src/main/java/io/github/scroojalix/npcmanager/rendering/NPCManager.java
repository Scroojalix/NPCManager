package io.github.scroojalix.npcmanager.rendering;

import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.npc.HologramContainer;
import io.github.scroojalix.npcmanager.npc.NPCContainer;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.npc.NPCTrait;
import io.github.scroojalix.npcmanager.npc.skin.SkinData;
import io.github.scroojalix.npcmanager.npc.skin.SkinManager;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

/**
 * The interface that contains all methods to be used in an NPCManger class
 * 
 * @author Scroojalix
 */
public class NPCManager {

	private NPCMain main;
	private Map<String, NPCContainer> NPCs = new LinkedHashMap<String, NPCContainer>();
	private boolean fetchDefaultSkins;
	private int npcNameLength;
	private ProtocolManager protocolManager;

	private Random random;

	public NPCManager(NPCMain main) {
		this.main = main;
		this.protocolManager = ProtocolLibrary.getProtocolManager();
		fetchDefaultSkins = main.getConfig().getBoolean("fetch-default-skins");
		npcNameLength = main.getConfig().getInt("npc-name-length");
		if (npcNameLength > 16)
			npcNameLength = 16;
		if (npcNameLength < 3)
			npcNameLength = 3;
		main.log(Level.INFO, "Set NPC tab list name length to " + npcNameLength);

		this.random = new Random(6878);
	}

	public void setNPCNameLength(int npcNameLength) {
		this.npcNameLength = npcNameLength;
	}

	/**
	 * Getter for NPCs hashmap.
	 * Please do not use this function anywhere but the PluginUtils class.
	 * @return NPCs HashMap
	 */
	public Map<String, NPCContainer> getNPCHashMap() {
		return NPCs;
	}

	public void createNPC(String name, Location loc, boolean store) {
		NPCData data = new NPCData(name, loc, store);
		main.storage.saveNPC(data);
		spawnNPC(data);
		// FIXME skin manager stuff called twice? see spawnNPC()
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
		NPCContainer npcContainer = createNPCData(data);
		NPCs.put(data.getName(), npcContainer);
		startLoaderTask(npcContainer);
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
		// FIXME removeNPC() should take in npcContainer as input, not name.
		for (NPCContainer container : NPCs.values()) {
			removeNPCInternal(container.getNPCData().getName(), false);
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
		removeNPCInternal(npc, fromStorage);
		NPCs.remove(npc);
	}

	/**
	 * Deletes an NPC, without removing it from the NPCs array.
	 * This is done to avoid a ConcurrentModificationException
	 * @param npc
	 * @param fromStorage
	 */
	private void removeNPCInternal(String npc, boolean fromStorage) {
		NPCContainer container = NPCs.get(npc);
		Bukkit.getScheduler().cancelTask(container.getLoaderTaskID());
		container.getLoaderTask().clearAllTasks();
		for (Player p : Bukkit.getOnlinePlayers()) {
			container.getLoaderTask().sendDeletePackets(p);
		}
		if (fromStorage && container.getNPCData().isStored()) {
			main.storage.removeNPC(container.getNPCData().getName());
		}
	}

	/**
	 * Converts all data in the NPCData object into NMS stuff.
	 * @param data The {@link NPCData} to create NMS data from.
	 */
	public NPCContainer createNPCData(NPCData data) {
		NPCContainer container = new NPCContainer(data, nextEntityId());

		//NPC
		NPCTrait traits = data.getTraits();
		WrappedGameProfile profile = new WrappedGameProfile(data.getUUID(), getRandomNPCName());
		SkinData skin = traits.getSkinData();
		if (skin != null && skin.getTexture() != null && skin.getSignature() != null) {
			profile.getProperties().put("textures",
					new WrappedSignedProperty("textures", skin.getTexture(), skin.getSignature()));
		}
		PlayerInfoData infoData = new PlayerInfoData(
			profile,
			0,
			EnumWrappers.NativeGameMode.SURVIVAL,
			WrappedChatComponent.fromText(
				PluginUtils.format("&8[NPC] "+profile.getName())));
		container.setPlayerInfo(infoData);

		//Holograms
		String displayName = data.getTraits().getDisplayName();
		String subtitle = data.getTraits().getSubtitle();

		boolean hasDisplayName = displayName != null;
		boolean hasSubtitle = subtitle != null;
		Location loc = data.getLoc();
		Location upperLoc = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1.95, loc.getZ());
		Location lowerLoc = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1.7, loc.getZ());
		if (hasDisplayName && hasSubtitle) {
			container.setNameHolo(new HologramContainer(nextEntityId(), upperLoc, displayName));
			container.setSubtitleHolo(new HologramContainer(nextEntityId(), lowerLoc, subtitle));
		} else if (hasDisplayName && !hasSubtitle) {
			container.setNameHolo(new HologramContainer(nextEntityId(), lowerLoc, displayName));
			container.setSubtitleHolo(null);
		} else if (!hasDisplayName && hasSubtitle) {
			container.setNameHolo(null);
			container.setSubtitleHolo(new HologramContainer(nextEntityId(), lowerLoc, subtitle));
		} else {
			container.setNameHolo(null);
			container.setSubtitleHolo(null);
		}

		return container;
	}

	// TODO need a function that gets the next Entity Id
	// May need to use reflection on Entity#ENTITY_COUNTER
	// https://www.spigotmc.org/threads/create-new-entityid.557198/
	// For now, a random large integer will do
	private int nextEntityId() {
		return random.nextInt() & Integer.MAX_VALUE;
	}

	/**
	 * Spawns an NPC, assuming all the NMS code has been generated.
	 * @param data The NPC to spawn.
	 */
	public void startLoaderTask(NPCContainer container) {
		NPCLoader loader = new NPCLoader(main, container, protocolManager);
		int taskId = Bukkit.getScheduler().scheduleSyncRepeatingTask(main, loader, 0l, 1l);
		container.setLoaderTask(loader, taskId);
	}

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
