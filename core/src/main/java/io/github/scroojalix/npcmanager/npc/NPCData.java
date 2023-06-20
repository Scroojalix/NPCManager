package io.github.scroojalix.npcmanager.npc;

import java.util.Map;
import java.util.UUID;

import org.bukkit.Location;

import com.google.gson.annotations.Expose;

import io.github.scroojalix.npcmanager.storage.misc.Serialisable;

/**
 * Class that stores all of an NPC's data.
 * @author Scroojalix
 */
public class NPCData implements Serialisable {

	//TODO add createdby field
	//Use this to add the feature of restricting the amount of NPC's each player can spawn to a
	//predetermined value from config.yml. May be used in survival servers to customise bases.
	
	@Expose
	private String name;
	@Expose
	private Location loc;
	@Expose
	private String uuid;
	@Expose
	private NPCTrait traits;
		
	private boolean store;
	private boolean loaded;

	NPCData() {}

	public NPCData(String name, Location loc, boolean store) {
		this(name, loc, 60, true, store);
	}

	public NPCData(String name, Location loc, int range, boolean headRotation, boolean store) {
		this.name = name;
		setLoc(loc);
		this.uuid = UUID.randomUUID().toString();
		this.traits = new NPCTrait(name, range, headRotation);
		this.store = store;
	}

	/**
	 * @return The traits of this NPC.
	 */
	public NPCTrait getTraits() {
		return traits;
	}
	
	/** 
	 * @return Name of this NPC.
	 */
	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public UUID getUUID() {
		return UUID.fromString(uuid);
	}
	
	/**
	 * @return Location of this NPC.
	 */
	public Location getLoc() {
		return loc;
	}
	
	/**
	 * Sets the location of this NPC.
	 * @param loc New Location
	 */
	public void setLoc(Location loc) {
		Map<String, Object> newLoc = loc.serialize();
		newLoc.put("x", (double)((int)(((double)newLoc.get("x"))*100))/100);
		newLoc.put("y", (double)((int)(((double)newLoc.get("y"))*100))/100);
		newLoc.put("z", (double)((int)(((double)newLoc.get("z"))*100))/100);
		newLoc.put("yaw", (float)((int)(((float)newLoc.get("yaw"))*100))/100);
		newLoc.put("pitch", (float)((int)(((float)newLoc.get("pitch"))*100))/100);
		this.loc = Location.deserialize(newLoc);
	}
	
	public boolean isStored() {
		return store;
	}

	public void setStored(boolean store) {
		this.store = store;
	}

	public boolean isLoaded() {
		return this.loaded;
	}

	public void setLoaded(boolean loaded) {
		this.loaded = loaded;
	}
}