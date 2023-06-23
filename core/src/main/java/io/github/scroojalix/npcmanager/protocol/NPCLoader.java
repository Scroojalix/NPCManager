package io.github.scroojalix.npcmanager.protocol;

import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.PacketContainer;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.npc.NPCContainer;
import io.github.scroojalix.npcmanager.utils.PluginUtils;
import io.github.scroojalix.npcmanager.utils.Settings;

public class NPCLoader implements Runnable {

	private NPCMain main;
	private final NPCContainer npcContainer;
	private ProtocolManager pm;

	private HashMap<Player, Integer> loadedForPlayers = new HashMap<Player, Integer>();
	private HashSet<Player> outsideHeadRotationRange = new HashSet<Player>();
	private LinkedHashSet<PacketContainer> loadPackets = new LinkedHashSet<PacketContainer>();

	private final double range;
	private final boolean hasHeadRotation;
	private final double headRotationRange;
	private final boolean resetRotation;
	private final boolean perfectOrientation;
	private final long npcRemoveDelay;
	private final Location npcLoc;

	public NPCLoader(NPCMain main, NPCContainer npcContainer, ProtocolManager protocolManager) {
		this.main = main;
		this.npcContainer = npcContainer;
		this.pm = protocolManager;
		this.range = npcContainer.getNPCData().getTraits().getRange();
		this.hasHeadRotation = npcContainer.getNPCData().getTraits().hasHeadRotation();
		this.npcLoc = npcContainer.getNPCData().getLoc();

		this.headRotationRange = Settings.HEAD_ROTATION_RANGE.get();
		this.resetRotation = Settings.RESET_HEAD_ROTATION.get();
		this.perfectOrientation = Settings.PERFECT_HEAD_ROTATION.get();
		this.npcRemoveDelay = Settings.NPC_REMOVE_DELAY.get();

		generatePackets();
	}

	/**
	 * Terminates all Bukkit Runnable tasks so this NPCLoader task can be terminated.
	 */
	public void clearAllTasks() {
		for (int id : loadedForPlayers.values()) {
			Bukkit.getScheduler().cancelTask(id);
		}
		loadedForPlayers.clear();
		outsideHeadRotationRange.clear();
	}

	/**
	 * Generate all packets required to spawn an NPC, and store them in a LinkedHashSet.
	 */
	private void generatePackets() {
		loadPackets.add(PacketRegistry.NPC_ADD_INFO.get(npcContainer));
		loadPackets.add(PacketRegistry.NPC_SPAWN.get(npcContainer));
		loadPackets.add(PacketRegistry.NPC_UPDATE_METADATA.get(npcContainer));
		loadPackets.addAll(PacketRegistry.NPC_RESET_HEAD_ROTATION.get(npcContainer));

		//Scoreboards
		// FIXME these packets don't need to be sent every time an NPC is loaded
		loadPackets.add(PacketRegistry.SCOREBOARD_CREATE.get());
		loadPackets.add(PacketRegistry.SCOREBOARD_ADD_NPC.get(npcContainer));
		
		if (perfectOrientation) {
			loadPackets.add(PacketRegistry.NPC_PLAY_ANIMATION.get(npcContainer));
		}

		//Holograms
		if (npcContainer.isNameHoloEnabled()) {
			loadPackets.addAll(PacketRegistry.HOLOGRAM_CREATE.get(npcContainer.getNameHolo()));
		}
		if (npcContainer.isSubtitleHoloEnabled()) {
			loadPackets.addAll(PacketRegistry.HOLOGRAM_CREATE.get(npcContainer.getSubtitleHolo()));
		}

		//Equipment
		if (npcContainer.getNPCData().getTraits().getEquipment(false) != null) {
			loadPackets.addAll(PacketRegistry.NPC_SET_EQUIPMENT.get(npcContainer));
		}
	}

	/**
	 * Method that loops to update NPC's.
	 */
	public void run() {
		for (Player player : Bukkit.getOnlinePlayers()) {
			if (player.getWorld().getName().equalsIgnoreCase(npcLoc.getWorld().getName())) {
				double distance = calculateDistance(npcLoc, player.getLocation());
				if (distance <= range) {
					if (!loadedForPlayers.containsKey(player)) {
						sendLoadPackets(player);
					}
					if (hasHeadRotation) {
						if (distance <= headRotationRange && distance > 0) {
							if (outsideHeadRotationRange.contains(player)) {
								outsideHeadRotationRange.remove(player);
							}
							lookInDirection(player);
						} else if (resetRotation) {
							if (!outsideHeadRotationRange.contains(player)) {
								outsideHeadRotationRange.add(player);
								resetLookDirection(player);
							}
						}
					}
				} else if (loadedForPlayers.containsKey(player)) {
					Bukkit.getScheduler().cancelTask(loadedForPlayers.get(player));
					loadedForPlayers.remove(player);
					sendDeletePackets(player);
				}
			} else if (loadedForPlayers.containsKey(player)) loadedForPlayers.remove(player);
		}
	}

	/**
	 * Makes an NPC look towards a player.
	 * @param player The player to look at.
	 */
	private void lookInDirection(Player player) {
		// Source: https://stackoverflow.com/a/18185407
		double dX = player.getLocation().getX() - npcLoc.getX();
		double dY = player.getEyeLocation().getY() - (npcLoc.getY() + 1.62);
		double dZ = player.getLocation().getZ() - npcLoc.getZ();

		double yaw_temp = Math.atan2(dZ, dX);
		byte yaw = PluginUtils.toByteAngle(Math.toDegrees(yaw_temp) - 90);

		double pitch_temp = Math.atan2(Math.sqrt(dZ * dZ + dX * dX), dY);
		byte pitch = PluginUtils.toByteAngle(Math.toDegrees(pitch_temp) - 90);

		for (PacketContainer container : PacketRegistry.getHeadRotationPackets(npcContainer, yaw, pitch)) {
			pm.sendServerPacket(player, container);
		}
	}

	/**
	 * Reset an NPC's head rotation for a player.
	 * @param player The player to reset head rotation for.
	 */
	private void resetLookDirection(Player player) {
		for (PacketContainer container : PacketRegistry.NPC_RESET_HEAD_ROTATION.get(npcContainer)) {
			pm.sendServerPacket(player, container);
		}	
	}

	/**
	 * Send packets that spawn the NPC to a player.
	 * @param player The player to send packets to.
	 */
	private void sendLoadPackets(Player player) {
		for (PacketContainer container : loadPackets) {
			pm.sendServerPacket(player, container);
		}
		loadedForPlayers.put(player, Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
			@Override
			public void run() {
				pm.sendServerPacket(player, PacketRegistry.NPC_REMOVE_INFO.get(npcContainer));
			}
		}, npcRemoveDelay));
	}

	/**
	 * Send packets to a player to delete/hide an NPC.
	 * @param player
	 */
	public void sendDeletePackets(Player player) {
		pm.sendServerPacket(player, PacketRegistry.NPC_DESTROY.get(npcContainer));
		pm.sendServerPacket(player, PacketRegistry.NPC_REMOVE_INFO.get(npcContainer));
	}

	/**
	 * Calculate the distance between two locations.
	 * @param loc1 Location of NPC.
	 * @param loc2 Location of Player.
	 * @return The distance between an NPC and a Player.
	 */
	private double calculateDistance(Location loc1, Location loc2) {
        return Math.sqrt(Math.pow(loc1.getX() - loc2.getX(), 2) + Math.pow(loc1.getY() - loc2.getY(), 2) + Math.pow(loc1.getZ() - loc2.getZ(), 2));
	}
}
