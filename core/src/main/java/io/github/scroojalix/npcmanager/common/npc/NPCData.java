package io.github.scroojalix.npcmanager.common.npc;

import java.util.Map;
import java.util.UUID;

import com.google.gson.annotations.Expose;

import org.bukkit.Location;

import io.github.scroojalix.npcmanager.nms.interfaces.NMSHologram;
import io.github.scroojalix.npcmanager.common.interactions.CommandInteraction;
import io.github.scroojalix.npcmanager.common.interactions.InteractEvent;
import io.github.scroojalix.npcmanager.common.interactions.InteractEventType;
import io.github.scroojalix.npcmanager.common.storage.misc.Serialisable;

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
	
	//Holograms
	//TODO make this into an array to allow multiple lines
	private NMSHologram nameHolo;
	private NMSHologram subtitleHolo;

	private InteractEvent interactEvent;
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
	
	/**
	 * Sets the name hologram of this NPC.
	 * @param hologram The new name Hologram
	 */
	public void setNameHolo(NMSHologram hologram) {
		this.nameHolo = hologram;
	}
	
	/**
	 * @return The name hologram of this NPC.
	 */
	public NMSHologram getNameHolo() {
		return nameHolo;
	}
	
	/**
	 * Sets the subtitle hologram of this NPC.
	 * @param hologram The new name Hologram
	 */
	public void setSubtitleHolo(NMSHologram hologram) {
		this.subtitleHolo = hologram;
	}
	
	/**
	 * @return The subtitle hologram of this NPC.
	 */
	public NMSHologram getSubtitleHolo() {
		return subtitleHolo;
	}	
	
	/**
	 * Sets the Interact Event of this NPC.
	 * @param interactEvent New InteractEvent
	 */
	public void setInteractEvent(InteractEvent interactEvent) {
		this.interactEvent = interactEvent;
		if (interactEvent != null) {
			if (interactEvent instanceof CommandInteraction) {
				traits.setInteractEvent(InteractEventType.COMMAND, ((CommandInteraction)interactEvent).getCommand());
			} else {
				traits.setInteractEvent(InteractEventType.CUSTOM, interactEvent.getInteractionName());
			}
		} else {
			traits.removeInteractEvent();;
		}
	}
	
	/**
	 * @return The Interact Event of this NPC.
	 */
	public InteractEvent getInteractEvent() {
		return interactEvent;
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