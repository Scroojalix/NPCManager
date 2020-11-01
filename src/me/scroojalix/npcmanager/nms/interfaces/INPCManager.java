package me.scroojalix.npcmanager.nms.interfaces;

import java.io.File;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.logging.Level;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

import me.scroojalix.npcmanager.NPCMain;
import me.scroojalix.npcmanager.api.InteractionsManager;
import me.scroojalix.npcmanager.utils.FileManager;
import me.scroojalix.npcmanager.utils.NPCData;
import net.md_5.bungee.api.ChatColor;

/** 
 * The interface that contains all methods to be used in an NPCManger class
 * @author Scroojalix
 */
public abstract class INPCManager {
	
	protected NPCMain main;
	protected Set<String> hiddenNPCs = new HashSet<String>();
	protected Map<String, NPCData> NPCs;
	
	/**
	 * Returns the Hash Map containing all NPC's
	 * @return The Hash Map which stores the NPC's in memory.
	 */
	public Map<String, NPCData> getNPCs() {
		return NPCs;
	}
	
	/**
	 * @return The list of NPCs that have their name tags hidden.
	 */
	public Set<String> getHiddenNPCs() {
		return hiddenNPCs;
	}
	
	/**
	 * Creates an NPC with the Name {@code name} and Location {@code loc}
	 * @param name - The name of the NPC, used to identify it.
	 * @param loc - The location of the NPC
	 */
	public void createNPC(String name, Location loc) {
		String displayName = ChatColor.stripColor(main.format(name)).isEmpty()?"Default":name;
		NPCData data = new NPCData(name, displayName, null, null, loc, 60, true);
		restoreNPC(data);
	}
	
	/**
	 * Updates an NPC with the NPCData {@code data}
	 * @param data - The NPCData assigned to an NPC.
	 */
	public void updateNPC(NPCData data) {
		removeNPC(data.getName());
		restoreNPC(data);
	}
	
	/**
	 * Moves an NPC with Data {@code} to Location {@code loc}
	 * @param data - The NPCData assigned to an NPC
	 * @param loc - The desired location
	 */
	public abstract void moveNPC(NPCData data, Location loc);
	
	/**
	 * Sets the skin of an NPC with Data {@code data} to Skin {@code skin}
	 * Skins are defined in the skins.yml file
	 * @param data - The NPCData assigned to an NPC
	 * @param skin - The name of the desired skin
	 */
	public void setSkin(NPCData data, String skin) {
		removeNPC(data.getName());
		NPCs.remove(data.getName());
		data.getTraits().setSkin(skin);		
		restoreNPC(data);
	}
	
	/**
	 * Deletes an NPC with the name {@code name}
	 * @param name - The name of the NPC to be deleted
	 */
	public abstract void removeNPC(String name);
	
	/**
	 * Saves all NPC's using the saving method defined in the config.yml
	 */
	public void saveNPCs() {
		switch(main.saveMethod) {
		case YAML:
			if (!main.npc.getNPCs().isEmpty()) {
				saveYAMLNPCs(main.npcFile);
				NPCs.clear();
			}
			break;
		case MYSQL:
			if (main.sql.getGetter().testConnection()) {
				for (NPCData data : NPCs.values()) {
					if (!data.isWorldNull()) {
						main.sql.getGetter().addNPC(data);
					} else {
						main.log(Level.WARNING, "Could not save NPC '"+data.getName()+"'. That world does not exist.");
					}
					removeNPC(data.getName());
				}
				main.sql.disconnect();
			} else {
				main.log(Level.SEVERE, "Could not save NPCs to database.");
				if (!NPCs.isEmpty()) {
					main.log(Level.SEVERE, "Saving NPC's to temp.yml instead.");
					saveYAMLNPCs(new FileManager(main, "temp.yml"));
				}
			}
			NPCs.clear();
			break;
		}
	}
	
	/**
	 * Saves all NPC's to the file npcs.yml
	 * @param file The file to save to.
	 */
	public void saveYAMLNPCs(FileManager file) {
		FileConfiguration data = file.getConfig();
		for (Map.Entry<String, NPCData> entry : NPCs.entrySet()) {
			if (entry.getValue().getLoc() != null) {
				NPCData npc = entry.getValue();
				data.set("npc."+entry.getKey()+".world", npc.getLoc().getWorld().getName());
				data.set("npc."+entry.getKey()+".x", npc.getLoc().getX());
				data.set("npc."+entry.getKey()+".y", npc.getLoc().getY());
				data.set("npc."+entry.getKey()+".z", npc.getLoc().getZ());
				data.set("npc."+entry.getKey()+".yaw", npc.getLoc().getYaw());
				data.set("npc."+entry.getKey()+".pitch", npc.getLoc().getPitch());
				if (npc.getTraits().getDisplayName() != null) data.set("npc."+entry.getKey()+".displayName", npc.getTraits().getDisplayName());
				if (npc.getTraits().getSubtitle() != null) data.set("npc."+entry.getKey()+".subtitle", npc.getTraits().getSubtitle());
				if (npc.getTraits().getSkin() != null) data.set("npc."+entry.getKey()+".skin", npc.getTraits().getSkin());
				else data.set("npc."+entry.getKey()+".id", npc.getUUID());
				if (npc.getInteractEvent() != null) data.set("npc."+entry.getKey()+".interactEvent", npc.getInteractEvent().getInteractionName().replace(" ", "-"));
				data.set("npc."+entry.getKey()+".range", npc.getTraits().getRange());
				data.set("npc."+entry.getKey()+".hasHeadRotation", npc.getTraits().hasHeadRotation());
			} else {
				main.log(Level.WARNING, "Could not save '"+entry.getKey()+"' NPC: Unknown world");
			}
			removeNPC(entry.getKey());
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
						restoreYAMLNPCs(main.npcFile);
						main.log(Level.INFO, "Done");
					}
					break;
				case MYSQL:
					if (main.sql.getGetter().testConnection()) {
						main.log(Level.INFO, "Restoring NPCs...");
						main.sql.getGetter().restoreDataEntries();
						main.sql.getGetter().emptyTable();
						main.log(Level.INFO, "Done");
					}
					File file = new File(main.getDataFolder(), "temp.yml");
					if (file.exists()) {
						main.log(Level.INFO, "Restoring NPC's from temp.yml");
						restoreYAMLNPCs(new FileManager(main, "temp.yml"));
						file.delete();
						main.log(Level.INFO, "Done");
					}
					break;
				}
			}
		}, 10);
	}
	
	/**
	 * Restores all NPC's from npcs.yml
	 * @param The file to restore from.
	 */
	public void restoreYAMLNPCs(FileManager file) {
		FileConfiguration data = file.getConfig();
		data.getConfigurationSection("npc").getKeys(false).forEach(current -> {
			String worldName = data.getString("npc."+current+".world");
			if (Bukkit.getWorld(worldName) != null) {
				Location loc = new Location(Bukkit.getWorld(worldName),
						data.getDouble("npc."+current+".x"),
						data.getDouble("npc."+current+".y"),
						data.getDouble("npc."+current+".z"),
						Float.valueOf(data.getString("npc."+current+".yaw")),
						Float.valueOf(data.getString("npc."+current+".pitch")));
				
				String displayName;
				if (data.isSet("npc."+current+".displayName")) displayName = data.getString("npc."+current+".displayName");
				else displayName = null;
				
				String subtitle;
				if (data.isSet("npc."+current+".subtitle")) subtitle = data.getString("npc."+current+".subtitle");
				else subtitle = null;
				
				int range = data.getInt("npc."+current+".range");
				boolean hasHeadRotation = data.getBoolean("npc."+current+".hasHeadRotation");
				
				NPCData npcdata = new NPCData(current, displayName, subtitle, null, loc, range, hasHeadRotation);
				if (data.isSet("npc."+current+".skin")) npcdata.getTraits().setSkin(data.getString("npc."+current+".skin"));
				if (data.isSet("npc."+current+".id")) npcdata.setUUID(data.getString("npc."+current+".id"));
				if (data.isSet("npc."+current+".interactEvent")) {
					String interactEvent = data.getString("npc."+current+".interactEvent");
					if (InteractionsManager.getInteractEvents().containsKey(interactEvent)) {
						npcdata.setInteractEvent(InteractionsManager.getInteractEvents().get(interactEvent));
					} else {
						main.log(Level.WARNING, "Error whilst restoring NPCs: Unknown interact event '"+interactEvent+"'");
					}
				}
				restoreNPC(npcdata);
			} else {
				main.log(Level.WARNING, "Could not restore '"+current+"' NPC - Unknown World '"+worldName+"'");
			}
		});
		data.set("npc", null);
		file.saveConfig();
	}
	
	/**
	 * Creates a new NMS EntityPlayer for the NPCData object.
	 * @param data The {@link NPCData} to create an NMS NPC from.
	 */
	public abstract void getNMSEntity(NPCData data);
	
	/**
	 * Restores a specific NPC with Data {@code data}
	 * @param data - The data of the NPC to be restored
	 */
	public abstract void restoreNPC(NPCData data);
	
	/**
	 * Remove a hologram for a player
	 * @param player - The player to remove the hologram from.
	 * @param hologram - The hologram to remove.
	 */
	public abstract void removeHologramForPlayer(Player player, NMSHologram hologram);
}
