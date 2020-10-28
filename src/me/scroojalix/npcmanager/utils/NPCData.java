package me.scroojalix.npcmanager.utils;

import java.text.DecimalFormat;
import java.util.Map;

import org.bukkit.Bukkit;
import org.bukkit.Location;

import com.google.gson.GsonBuilder;
import com.google.gson.annotations.Expose;

import me.scroojalix.npcmanager.api.InteractEvent;
import me.scroojalix.npcmanager.nms.interfaces.NMSHologram;

public class NPCData {
	
	@Expose
	private String name;
	@Expose
	private String displayName;
	@Expose
	private String subtitle;
	@Expose
	private Map<String, Object> loc;
	@Expose
	private int range;
	@Expose
	private boolean headRotation;
	@Expose
	private String uuid;
	@Expose
	private String skin;
	@Expose
	private String interactEventName;
	
	private NMSHologram nameHolo;
	private NMSHologram subtitleHolo;
	
	private InteractEvent interactEvent;
	private Object npc;
	private int headRotationTask;
	private int loaderTask;

	public NPCData(String name, String displayName, Object npc, Location loc, int range, boolean headRotation) {
		this.name = name;
		this.displayName = displayName;
		this.npc = npc;
		setLoc(loc);
		this.range = range;
		this.headRotation = headRotation;
	}
	
	public String toJson() {
		return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().serializeNulls().create().toJson(this);
	}
	
	/** 
	 * @return Name of this NPC.
	 */
	public String getName() {
		return name;
	}
	
	/**
	 * @return Display Name of this NPC
	 */
	public String getDisplayName() {
		return displayName;
	}
	
	/**
	 * Sets the Display Name of this NPC.
	 * @param displayName - new Display Name
	 */
	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}
	
	/**
	 * @return Subtitle of this NPC.
	 */
	public String getSubtitle() {
		return subtitle;
	}
	
	/**
	 * Sets the subtitle of this NPC.
	 * @param subtitle - new Subtitle
	 */
	public void setSubtitle(String subtitle) {
		this.subtitle = subtitle;
	}
	
	/**
	 * @return EntityPlayer instance
	 */
	public Object getNPC() {
		return npc;
	}
	
	/**
	 * Sets the EntityPlayer instance of this NPC.
	 * @param npc - The instance of EntityPlayer
	 */
	public void setNPC(Object npc, String uuid) {
		this.npc = npc;
		this.uuid = uuid;
	}
	
	/**
	 * @return Location of this NPC.
	 */
	public Location getLoc() {
		try {
			return Location.deserialize(loc);
		} catch(IllegalArgumentException e) {
			return null;
		}
	}
	
	/**
	 * Sets the location of this NPC.
	 * @param loc - new Location
	 */
	public void setLoc(Location loc) {
		DecimalFormat df = new DecimalFormat("#.##");
		Map<String, Object> newLoc = loc.serialize();
		newLoc.put("x", Double.valueOf(df.format((double)newLoc.get("x"))));
		newLoc.put("y", Double.valueOf(df.format((double)newLoc.get("y"))));
		newLoc.put("z", Double.valueOf(df.format((double)newLoc.get("z"))));
		newLoc.put("pitch", Float.valueOf(df.format((float)newLoc.get("pitch"))));
		newLoc.put("yaw", Float.valueOf(df.format((float)newLoc.get("yaw"))));
		this.loc = newLoc;
	}
	
	/**
	 * Range is the distance a player must be to this NPC for it to be visible.
	 * @return The range of this NPC.
	 */
	public int getRange() {
		return range;
	}
	
	/**
	 * Sets the range of this NPC.
	 * @param range - new Range
	 */
	public void setRange(int range) {
		this.range = range;
	}
	
	/**
	 * Returns whether or not this NPC has Head Rotation.
	 * @return True/False
	 */
	public boolean hasHeadRotation() {
		return headRotation;
	}
	
	/**
	 * Sets whether or not this NPC has Head Rotation.
	 * @param headRotation - True/False
	 */
	public void setHasHeadRotation(boolean headRotation) {
		this.headRotation = headRotation;
	}
	
	/**
	 * @return UUID of NPC
	 */
	public String getUUID() {
		return uuid;
	}
	
	/**
	 * Sets the UUID string of this NPC. (Used during restoring)
	 */
	public void setUUID(String uuid) {
		this.uuid = uuid;
	}
	
	/**
	 * @return The name of the skin on this NPC.
	 */
	public String getSkin() {
		return skin;
	}
	
	/**
	 * Sets this NPC's skin.
	 * @param skin - new Skin
	 */
	public void setSkin(String skin) {
		this.skin = skin;
	}
	
	/**
	 * Sets the name hologram of this NPC.
	 * @param hologram - The new name Hologram
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
	 * @param hologram - The new name Hologram
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
	 * Sets the Integer assigned to the head rotation task of this NPC.
	 * @param task - Integer assigned to Head Rotation task.
	 */
	public void setHeadRotationTask(int task) {
		this.headRotationTask = task;
	}
	
	/**
	 * @return Integer assigned to Head Rotation task of this NPC.
	 */
	public int getHeadRotationTask() {
		return headRotationTask;
	}
	
	/**
	 * Sets the Integer assigned to the loader task of this NPC.
	 * @param task - Integer assigned to Loader task.
	 */
	public void setLoaderTask(int task) {
		this.loaderTask = task;
	}
	
	/**
	 * @return Integer assigned to Loader task of this NPC.
	 */
	public int getLoaderTask() {
		return loaderTask;
	}
	
	/**
	 * Sets the Interact Event of this NPC.
	 * @param interactEvent - new InteractEvent
	 */
	public void setInteractEvent(InteractEvent interactEvent) {
		this.interactEvent = interactEvent;
		if (interactEvent != null) {
			this.interactEventName = interactEvent.getInteractionName().replace(" ", "-");
		} else {
			this.interactEventName = null;
		}
	}
	
	/**
	 * @return The Interact Event of this NPC.
	 */
	public InteractEvent getInteractEvent() {
		return interactEvent;
	}
	
	/**
	 * @return The name of this NPC's interact event.
	 */
	public String getInteractEventName() {
		return interactEventName;
	}
	
	/**
	 * @return is the world that this NPC is in null?
	 */
	public boolean isWorldNull() {
		return Bukkit.getWorld(loc.get("world").toString()) == null;
	}
	
}