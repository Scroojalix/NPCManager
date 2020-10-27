package me.scroojalix.npcmanager.nms.interfaces;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.entity.Player;

import me.scroojalix.npcmanager.NPCMain;
import me.scroojalix.npcmanager.utils.NPCData;

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
		this.range = data.getRange();
		this.hasHeadRotation = data.hasHeadRotation();
		this.headRotationRange = main.getConfig().getDouble("npc-headrotation-range");
		this.resetRotation = main.getConfig().getBoolean("reset-headrotation");
	}
	
	protected abstract void generatePackets();
	
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
		} else removeNPC();
	}
	
	protected abstract void lookInDirection(Player player);
	
	protected abstract void resetLookDirection(Player player);
	
	protected abstract void sendLoadPackets(Player player);
	
	protected abstract void sendDeletePackets(Player player);
	
	protected abstract void removeNPC();
	
	protected double calculateDistance(Location loc1, Location loc2) {
        return Math.sqrt(Math.pow(loc1.getX() - loc2.getX(), 2) + Math.pow(loc1.getY() - loc2.getY(), 2) + Math.pow(loc1.getZ() - loc2.getZ(), 2));
    }
}
