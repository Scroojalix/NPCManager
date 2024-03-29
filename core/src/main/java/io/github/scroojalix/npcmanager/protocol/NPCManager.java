package io.github.scroojalix.npcmanager.protocol;

import java.security.SecureRandom;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.List;
import java.util.Map;
import java.util.logging.Level;
import java.util.stream.Collectors;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;
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
import io.github.scroojalix.npcmanager.npc.interactions.CommandInteraction;
import io.github.scroojalix.npcmanager.npc.interactions.InteractionsManager;
import io.github.scroojalix.npcmanager.npc.interactions.NPCInteractionData;
import io.github.scroojalix.npcmanager.npc.skin.SkinData;
import io.github.scroojalix.npcmanager.npc.skin.SkinManager;
import io.github.scroojalix.npcmanager.utils.Messages;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.Settings;

/**
 * The interface that contains all methods to be used in an NPCManger class
 * 
 * @author Scroojalix
 */
public class NPCManager {

	private NPCMain main;
	private Map<String, NPCContainer> NPCs = new LinkedHashMap<String, NPCContainer>();
	private ProtocolManager protocolManager;
	
	private ProtocolManager pm;
	private final LinkedHashSet<PacketContainer> scoreboardPackets;

	public NPCManager(NPCMain main) {
		this.main = main;
		this.protocolManager = ProtocolLibrary.getProtocolManager();
		main.sendDebugMessage(Level.INFO, "NPC tab list name length set to " + Settings.NPC_NAME_LENGTH.get());

		pm = ProtocolLibrary.getProtocolManager();
		scoreboardPackets = new LinkedHashSet<>();
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
		if (Settings.FETCH_DEFAULT_SKINS.get()) {
			SkinManager.setSkinFromUsername(null, data, name, false, true);
		}
		updateAndSendScoreboardPackets();
	}

	/**
	 * Updates an NPC with the NPCData {@code data}
	 * Also updates the saved value in storage
	 * 
	 * @param data The NPCData assigned to an NPC.
	 */
	public void updateNPC(NPCData data) {
		main.storage.saveNPC(data);
		removeNPC(data.getName(), false);
		spawnNPC(data);
		updateAndSendScoreboardPackets();
	}

	/**
	 * Rename an NPC
	 * @param data
	 * @param newName
	 */
	public void renameNPC(NPCData data, String newName) {
		removeNPC(data.getName(), true);
		data.setName(newName);
		main.storage.saveNPC(data);
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
		main.sendDebugMessage(Level.INFO, String.format("Spawned %s with entity id %s", data.getName(), npcContainer.getNPCEntityID()));
	}

	/**
	 * Moves an NPC with Data {@code} to Location {@code loc}
	 * 
	 * @param data The NPCData assigned to an NPC
	 * @param loc  The desired location
	 */
	public void moveNPC(NPCData data, Location loc) {
		data.setLoc(loc);
		updateNPC(data);
	}

	/**
	 * Removes all NPCs.
	 */
	public void removeAllNPCs(boolean fromStorage) {
		for (NPCContainer container : NPCs.values()) {
			removeNPCInternal(container, fromStorage);
		}
		NPCs.clear();
		updateScoreboardPackets();
	}

	/**
	 * Deletes an NPC. Set {@code fromStorage} to true to also remove the NPC from
	 * storage.
	 * 
	 * @param npc         The name of the NPC to be deleted
	 * @param fromStorage Whether or not to remove the NPC from storage.
	 */
	public void removeNPC(String npc, boolean fromStorage) {
		removeNPCInternal(PluginUtils.getNPCContainerByName(npc), fromStorage);
		NPCs.remove(npc);
		updateScoreboardPackets();
	}

	/**
	 * Deletes an NPC, without removing it from the NPCs array.
	 * This is done to avoid a ConcurrentModificationException
	 * @param npc
	 * @param fromStorage
	 */
	private void removeNPCInternal(NPCContainer container, boolean fromStorage) {
		Bukkit.getScheduler().cancelTask(container.getLoaderTaskID());
		container.getLoaderTask().clearAllTasks();
		for (Player p : Bukkit.getOnlinePlayers()) {
			container.getLoaderTask().sendDeletePackets(p);
		}
		if (fromStorage && container.getNPCData().isStored()) {
			// Do this to comply with Nonnull
			String name = container.getNPCData().getName();
			if (name == null) return;
			main.storage.removeNPC(name);
		}
	}

	/**
	 * Converts all data in the NPCData object into NMS stuff.
	 * @param data The {@link NPCData} to create NMS data from.
	 */
	public NPCContainer createNPCData(NPCData data) {
		NPCContainer container = new NPCContainer(data);

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

		//Interact Event
		if (data.getTraits().getInteractEvent() != null) {
			NPCInteractionData interactEvent = data.getTraits().getInteractEvent();
			switch(interactEvent.getType()) {
				case COMMAND:
					container.setInteractEvent(new CommandInteraction(interactEvent.getValue()));
				break;
				case CUSTOM:
					if (InteractionsManager.getInteractEvents().containsKey(interactEvent.getValue())) {
						container.setInteractEvent(InteractionsManager.getInteractEvents().get(interactEvent.getValue()));
					} else {
						Messages.printNPCRestoreError(main, data.getName(), 
						"Error restoring an NPC: Unknown interact event '"+interactEvent.getValue()+"'");
					}
				break;
			}
		}

		//Holograms
		String displayName = data.getTraits().getDisplayName();
		String subtitle = data.getTraits().getSubtitle();

		boolean hasDisplayName = displayName != null;
		boolean hasSubtitle = subtitle != null;
		Location loc = data.getLoc();
		Location upperLoc = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1.95, loc.getZ());
		Location lowerLoc = new Location(loc.getWorld(), loc.getX(), loc.getY() + 1.7, loc.getZ());
		if (hasDisplayName && hasSubtitle) {
			container.setNameHolo(new HologramContainer(upperLoc, displayName));
			container.setSubtitleHolo(new HologramContainer(lowerLoc, subtitle));
		} else if (hasDisplayName && !hasSubtitle) {
			container.setNameHolo(new HologramContainer(lowerLoc, displayName));
			container.setSubtitleHolo(null);
		} else if (!hasDisplayName && hasSubtitle) {
			container.setNameHolo(null);
			container.setSubtitleHolo(new HologramContainer(lowerLoc, subtitle));
		} else {
			container.setNameHolo(null);
			container.setSubtitleHolo(null);
		}

		return container;
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

	private void updateScoreboardPackets() {
		scoreboardPackets.clear();

		List<String> npcNames = NPCs.values().stream()
			.map(npc -> npc.getPlayerInfo().getProfile().getName())
			.collect(Collectors.toList());

		scoreboardPackets.addAll(PacketRegistry.SCOREBOARD_CREATE_AND_ADD.get(npcNames));
	}

	public void sendScoreboardPackets(Player receiver) {
		for (PacketContainer packet : scoreboardPackets) {
			pm.sendServerPacket(receiver, packet);
		}
	}

	public void updateAndSendScoreboardPackets() {
		updateScoreboardPackets();
		for (Player p : Bukkit.getOnlinePlayers()) {
			sendScoreboardPackets(p);
		}
	}

	/**
	 * Generate a random string of characters to be used for NPC
	 * Profile names
	 * @return random string of characters with length {@code npcNameLength}
	 */
	private String getRandomNPCName() {
		char[] chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789_".toCharArray();
		SecureRandom rand = new SecureRandom();
		char[] result = new char[Settings.NPC_NAME_LENGTH.get()];
		for (int i = 0; i < result.length; i++) {
			result[i] = chars[rand.nextInt(chars.length)];
		}
		return new String(result);
	}
}
