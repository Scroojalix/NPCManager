package io.github.scroojalix.npcmanager.utils.npc;

import java.util.Map;
import java.util.UUID;
import java.util.logging.Level;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonObject;
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
import io.github.scroojalix.npcmanager.utils.interactions.InteractEventType;
import io.github.scroojalix.npcmanager.utils.interactions.InteractionsManager;
import io.github.scroojalix.npcmanager.utils.interactions.NPCInteractionData;
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
	private UUID uuid;
	@Expose
	private NPCTrait traits;
	
	//Holograms
	//TODO make this into an array to allow multiple lines
	private NMSHologram nameHolo;
	private NMSHologram subtitleHolo;

	private InteractEvent interactEvent;
	private Object npc;
	private int loaderTask;
	private boolean store;
	private boolean loaded;

	public NPCData(String name, Location loc, boolean store) {
		this(name, loc, 60, true, store);
	}

	public NPCData(String name, Location loc, int range, boolean headRotation, boolean store) {
		this.name = name;
		setLoc(loc);
		this.uuid = UUID.randomUUID();
		this.traits = new NPCTrait(name, range, headRotation);
		this.store = store;
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


	//TODO fix ItemStack not restoring properly (Loses itemmeta)
	//Custom banners throw errors to the console when restoring.
	/**
	 * Creates an NPCData object from a JSON string.
	 * @param json The JSON string to convert from.
	 * @return An NPCData object.
	 */
	public static NPCData fromJson(String name, String json, boolean prettyPrinting) {
		try {
			GsonBuilder builder = new GsonBuilder()
			.disableHtmlEscaping()
			.registerTypeHierarchyAdapter(ConfigurationSerializable.class, new ConfigurationSerializableAdapter());

			if (prettyPrinting) {
				builder.setPrettyPrinting();
			}
			
			JsonObject obj = new JsonParser().parse(json).getAsJsonObject();

			String world = obj.get("loc").getAsJsonObject().get("world").getAsString();
			if (Bukkit.getWorld(world) == null) {
				NPCMain.instance.npc.removeNPCFromStorage(name);
				NPCMain.instance.log(Level.SEVERE, "Error restoring an NPC: The world it's in does not exist.");
				NPCMain.instance.log(Level.SEVERE, "The NPC will be removed from storage.");
				return null;
			}

			NPCData data = builder.create().fromJson(json, NPCData.class);
			data.setStored(true);

			//Restore Interact Event
			if (data.getTraits().getInteractEvent() != null) {
				NPCInteractionData interactEvent = data.getTraits().getInteractEvent();
				if (interactEvent.getType() == InteractEventType.COMMAND) {
					data.setInteractEvent(new CommandInteraction(interactEvent.getValue()));
				} else if (InteractionsManager.getInteractEvents().containsKey(interactEvent.getValue())) {
					data.setInteractEvent(InteractionsManager.getInteractEvents().get(interactEvent.getValue()));
				} else {
					NPCMain.instance.log(Level.WARNING, "Error restoring an NPC: Unknown interact event '"+interactEvent.getValue()+"'");
					data.getTraits().removeInteractEvent();
				}
			}

			//TODO check if materials for equipment are suitable before returning.

			return data;
		} catch (JsonSyntaxException e) {
			NPCMain.instance.log(Level.SEVERE, "Error restoring an NPC: Invalid JSON");
			NPCMain.instance.log(Level.SEVERE, "The NPC will be removed from storage.");
			NPCMain.instance.npc.removeNPCFromStorage(name);
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

	public UUID getUUID() {
		return uuid;
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
	 */
	public void setNPC(Object npc) {
		this.npc = npc;
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