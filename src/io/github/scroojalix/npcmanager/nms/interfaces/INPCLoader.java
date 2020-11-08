package io.github.scroojalix.npcmanager.nms.interfaces;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.utils.NPCData;

public abstract class INPCLoader {
	
	protected NPCMain main;
	protected NPCData data;
	protected Map<Player, Integer> loadedForPlayers = new HashMap<Player, Integer>();
	protected Set<Player> outsideHeadRotationRange = new HashSet<Player>();
	
	private double range;
	private boolean hasHeadRotation;
	private double headRotationRange;
	private boolean resetRotation;
	
	public INPCLoader(NPCMain main, NPCData data) {
		this.main = main;
		this.data = data;
		this.range = data.getTraits().getRange();
		this.hasHeadRotation = data.getTraits().hasHeadRotation();
		this.headRotationRange = main.getConfig().getDouble("npc-headrotation-range");
		this.resetRotation = main.getConfig().getBoolean("reset-headrotation");
	}
	
	/**
	 * Generate all packets required to spawn an NPC, and store them in a LinkedHashSet.
	 */
	protected abstract void generatePackets();
	
	/**
	 * Method that loops to update NPC's.
	 */
	public void run() {
		if (data.getLoc() != null) {
			for (Player player : Bukkit.getOnlinePlayers()) {
				if (player.getWorld().getName().equalsIgnoreCase(data.getLoc().getWorld().getName())) {
					double distance = calculateDistance(data.getLoc(), player.getLocation());
					if (distance <= range) {
						if (!loadedForPlayers.containsKey(player)) {
							sendLoadPackets(player);
						}
						if (hasHeadRotation) {
							if (distance <= headRotationRange) {
								lookInDirection(player);
								if (outsideHeadRotationRange.contains(player)) {
									outsideHeadRotationRange.remove(player);
								}
							} else if (resetRotation) {
								if (!outsideHeadRotationRange.contains(player)) {
									resetLookDirection(player);
									outsideHeadRotationRange.add(player);
								}
							}
						}
					} else {
						if (loadedForPlayers.containsKey(player)) {
							sendDeletePackets(player);
						}
					}
				} else if (loadedForPlayers.containsKey(player)) loadedForPlayers.remove(player);
			}
		} else main.npc.removeNPC(data.getName(), true);
	}
	
	/**
	 * Makes an NPC look towards a player.
	 * @param player The player to look at.
	 */
	protected abstract void lookInDirection(Player player);
	
	/**
	 * Reset an NPC's head rotation for a player.
	 * @param player The player to reset head rotation for.
	 */
	protected abstract void resetLookDirection(Player player);
	
	/**
	 * Send packets that spawn the NPC to a player.
	 * @param player The player to send packets to.
	 */
	protected abstract void sendLoadPackets(Player player);

	/**
	 * Send packets to a player to delete/hide an NPC.
	 * @param player
	 */
	protected abstract void sendDeletePackets(Player player);
	
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
