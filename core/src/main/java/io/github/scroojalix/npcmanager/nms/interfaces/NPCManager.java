package io.github.scroojalix.npcmanager.nms.interfaces;

import java.security.SecureRandom;
import java.util.Collections;
import java.util.LinkedHashMap;
import java.util.Map;
import java.util.Random;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.wrappers.EnumWrappers;
import com.comphenix.protocol.wrappers.PlayerInfoData;
import com.comphenix.protocol.wrappers.WrappedChatComponent;
import com.comphenix.protocol.wrappers.WrappedGameProfile;
import com.comphenix.protocol.wrappers.WrappedSignedProperty;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.common.npc.NPCData;
import io.github.scroojalix.npcmanager.common.npc.NPCTrait;
import io.github.scroojalix.npcmanager.common.npc.skin.SkinData;
import io.github.scroojalix.npcmanager.common.npc.skin.SkinManager;

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

	public NPCManager(NPCMain main) {
		this.main = main;
		this.protocolManager = ProtocolLibrary.getProtocolManager();
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
		for (NPCContainer container : NPCs.values()) {
			removeNPC(container.getNPCData().getName(), false);
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
		NPCContainer container = NPCs.get(npc);
		Bukkit.getScheduler().cancelTask(container.getLoaderTaskID());
		container.getLoaderTask().clearAllTasks();
		for (Player p : Bukkit.getOnlinePlayers()) {
			sendRemoveNPCPackets(p, container);
			// TODO do holograms
			// if (data.getNameHolo() != null) {
			// 	removeHologramForPlayer(p, data.getNameHolo());
			// }
			// if (data.getSubtitleHolo() != null) {
			// 	removeHologramForPlayer(p, data.getSubtitleHolo());
			// }
		}
		if (fromStorage && container.getNPCData().isStored()) {
			main.storage.removeNPC(container.getNPCData().getName());
		}
		NPCs.remove(npc);
	}

	/**
	 * Send packets to a player which remove an NPC.
	 * 
	 * @param p    The player to send packets to.
	 * @param container The NPC to remove.
	 */
	public void sendRemoveNPCPackets(Player p, NPCContainer container) {
		PacketContainer destroyEntity = protocolManager.createPacket(PacketType.Play.Server.ENTITY_DESTROY);
		destroyEntity.getIntLists().write(0, Collections.singletonList(container.getEntityId()));

		// FIXME not compatible with older server versions
		PacketContainer removeInfo = protocolManager.createPacket(PacketType.Play.Server.PLAYER_INFO_REMOVE);
		removeInfo.getUUIDLists().write(0, Collections.singletonList(container.getNPCData().getUUID()));

		protocolManager.sendServerPacket(p, destroyEntity);
		protocolManager.sendServerPacket(p, removeInfo);
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
			profile.getProperties().put("textures", new WrappedSignedProperty("textures", skin.getTexture(), skin.getSignature()));
		}
        PlayerInfoData infoData = new PlayerInfoData(
			profile,
			0,
			EnumWrappers.NativeGameMode.SURVIVAL,
			WrappedChatComponent.fromText(profile.getName())
		);
        container.setPlayerInfo(infoData);

		//TODO hologram containers
		return container;
	}

	// need a function that gets the next Entity Id
    // May need to use reflection on Entity#ENTITY_COUNTER
    // https://www.spigotmc.org/threads/create-new-entityid.557198/
    // For now, a random large integer will do
    private int nextEntityId() {
        return new Random(83837).nextInt();
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
	
	/**
	 * Remove a hologram for a player
	 * @param player - The player to remove the hologram from.
	 * @param hologram - The hologram to remove.
	 */
	// TODO implement holograms in ProtocolLib
	// public abstract void removeHologramForPlayer(Player player, NMSHologram hologram);

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
