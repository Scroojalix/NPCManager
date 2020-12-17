package io.github.scroojalix.npcmanager.utils.npc;

import java.util.logging.Level;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonParser;
import com.google.gson.JsonSyntaxException;
import com.google.gson.annotations.Expose;

import org.bukkit.Bukkit;
import org.bukkit.Location;
import org.bukkit.configuration.serialization.ConfigurationSerializable;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.nms.interfaces.NMSHologram;
import io.github.scroojalix.npcmanager.utils.interactions.CommandInteraction;
import io.github.scroojalix.npcmanager.utils.interactions.InteractEvent;
import io.github.scroojalix.npcmanager.utils.interactions.InteractionsManager;
import io.github.scroojalix.npcmanager.utils.json.ConfigurationSerializableAdapter;

/**
 * Class that stores all of an NPC's data.
 * @author Scroojalix
 */
public class NPCData {
	
	@Expose
	private String name;
	@Expose
	private Location loc;
	@Expose
	private String uuid;
	@Expose
	private NPCTrait traits;
	
	//Holograms
	private NMSHologram nameHolo;
	private NMSHologram subtitleHolo;

	private InteractEvent interactEvent;
	private Object npc;
	private int loaderTask;
	private boolean store;

	public NPCData(String name, String displayName, Location loc, boolean store) {
		this(name, displayName, null, new NPCEquipment(), loc, 60, true, store);
	}

	public NPCData(String name, String displayName, String subtitle, NPCEquipment equipment, Location loc, int range, boolean headRotation, boolean store) {
		this.name = name;
		this.traits = new NPCTrait(displayName, subtitle, equipment, range, headRotation);
		this.store = store;
		setLoc(loc);
	}
	
	/**
	 * Converts this NPCData object to a JSON string.
	 * @return The JSON string.
	 */
	public String toJson(boolean prettyPrinting) {
		GsonBuilder builder = new GsonBuilder()
		.disableHtmlEscaping()
		.excludeFieldsWithoutExposeAnnotation()
		.registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ConfigurationSerializableAdapter());
		if (prettyPrinting) {
			builder.setPrettyPrinting();
		}
		return builder.create().toJson(this);
	}


	//TODO fix ItemStack and null not restoring properly (Loses enchantments)
	//TODO account for when an item is used that is not availabe in the current server version.
	/**
	 * Creates an NPCData object from a JSON string.
	 * @param json The JSON string to convert from.
	 * @return An NPCData object.
	 */
	public static NPCData fromJson(String json, boolean prettyPrinting) {
		try {
			GsonBuilder builder = new GsonBuilder()
			.disableHtmlEscaping()
			.registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ConfigurationSerializableAdapter());

			if (prettyPrinting) {
				builder.setPrettyPrinting();
			}
			
			String world = new JsonParser().parse(json).getAsJsonObject()
					.get("loc").getAsJsonObject().get("world").getAsString();
			
			if (Bukkit.getWorld(world) == null) {
				//TODO remove NPC from storage if world is null
				NPCMain.instance.log(Level.SEVERE, "Error restoring an NPC: The world it's in does not exist.");
				return null;
			}

			NPCData data = builder.create().fromJson(json, NPCData.class);
			data.setStored(true);

			//Restore Interact Event
			if (data.getTraits().getInteractEvent() != null) {
				String interactEvent = data.getTraits().getInteractEvent();
				if (interactEvent.startsWith("Command:")) {
					data.setInteractEvent(new CommandInteraction(interactEvent.replaceFirst("Command:", "")));
				} else if (InteractionsManager.getInteractEvents().containsKey(interactEvent)) {
					data.setInteractEvent(InteractionsManager.getInteractEvents().get(interactEvent));
				} else {
					NPCMain.instance.log(Level.WARNING, "Error restoring an NPC: Unknown interact event '"+interactEvent+"'");
					data.getTraits().setInteractEvent(null);
				}
			}

			return data;
		} catch (JsonSyntaxException e) {
			NPCMain.instance.log(Level.SEVERE, "Error restoring an NPC: Invalid JSON");
			return null;
		}
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

	/**
	 * @return EntityPlayer instance
	 */
	public Object getNPC() {
		return npc;
	}
	
	/**
	 * Sets the EntityPlayer instance of this NPC.
	 * @param npc The instance of EntityPlayer
	 * @param uuid The UUID of the NPC.
	 */
	public void setNPC(Object npc, String uuid) {
		this.npc = npc;
		this.uuid = uuid;
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
		this.loc = loc;
	}
	
	/**
	 * @return UUID of NPC
	 */
	public String getUUID() {
		return uuid;
	}
	
	/**
	 * Sets the UUID string of this NPC. (Used during restoring)
	 * @param uuid The UUID to be used when restoring this NPC.
	 */
	public void setUUID(String uuid) {
		this.uuid = uuid;
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
	 * Sets the Integer assigned to the loader task of this NPC.
	 * @param task Integer assigned to Loader task.
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
	 * @param interactEvent New InteractEvent
	 */
	public void setInteractEvent(InteractEvent interactEvent) {
		this.interactEvent = interactEvent;
		if (interactEvent != null) {
			if (interactEvent instanceof CommandInteraction) {
				traits.setInteractEvent("Command:"+((CommandInteraction)interactEvent).getCommand());
			} else {
				traits.setInteractEvent(interactEvent.getInteractionName());
			}
		} else {
			traits.setInteractEvent(null);
		}
	}
	
	/**
	 * @return The Interact Event of this NPC.
	 */
	public InteractEvent getInteractEvent() {
		return interactEvent;
	}
	
	/**
	 * @return Is the world that this NPC in null?
	 */
	public boolean isWorldNull() {
		return loc == null || loc.getWorld() == null;
	}

	public boolean isStored() {
		return store;
	}

	private void setStored(boolean store) {
		this.store = store;
	}
}