package io.github.scroojalix.npcmanager.storage.misc;

import java.util.Map;
import java.util.logging.Level;

import com.google.gson.GsonBuilder;
import com.google.gson.JsonSyntaxException;
import com.google.gson.reflect.TypeToken;

import org.bukkit.Bukkit;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.npc.NPCData;
import io.github.scroojalix.npcmanager.npc.interactions.CommandInteraction;
import io.github.scroojalix.npcmanager.npc.interactions.InteractEventType;
import io.github.scroojalix.npcmanager.npc.interactions.InteractionsManager;
import io.github.scroojalix.npcmanager.npc.interactions.NPCInteractionData;

public final class JsonParser {

    public static String toJson(NPCData data, boolean prettyPrinting) {
        GsonBuilder builder = new GsonBuilder()
		.disableHtmlEscaping();
		if (prettyPrinting) {
			builder.setPrettyPrinting();
		}
		return builder.create().toJson(data.serialise());
    }

    public static NPCData fromJson(String name, String json, boolean prettyPrinting) {
        try {
			GsonBuilder builder = new GsonBuilder()
			.disableHtmlEscaping();
			if (prettyPrinting) {
				builder.setPrettyPrinting();
			}
			
			Map<String, Object> obj = builder.create().fromJson(json, new TypeToken<Map<String, Object>>() {}.getType());

            //Check location
            @SuppressWarnings("unchecked")
            Map<String, Object> loc = (Map<String, Object>)obj.get("loc");
			String world = loc.get("world").toString();
			if (Bukkit.getWorld(world) == null) {
				NPCMain.instance.storage.removeNPC(name);
				NPCMain.instance.sendDebugMessage(Level.SEVERE, "Error restoring an NPC: The world it's in does not exist.");
				NPCMain.instance.sendDebugMessage(Level.SEVERE, "The NPC will be removed from storage.");
				return null;
			}

			NPCData data = Serialisable.deserialise(obj, NPCData.class);
			data.setStored(true);

			//Restore Interact Event
			if (data.getTraits().getInteractEvent() != null) {
				NPCInteractionData interactEvent = data.getTraits().getInteractEvent();
				if (interactEvent.getType() == InteractEventType.COMMAND) {
					data.setInteractEvent(new CommandInteraction(interactEvent.getValue()));
				} else if (InteractionsManager.getInteractEvents().containsKey(interactEvent.getValue())) {
					data.setInteractEvent(InteractionsManager.getInteractEvents().get(interactEvent.getValue()));
				} else {
					NPCMain.instance.sendDebugMessage(Level.WARNING, "Error restoring an NPC: Unknown interact event '"+interactEvent.getValue()+"'");
					data.getTraits().removeInteractEvent();
				}
			}

			return data;
		} catch (JsonSyntaxException | IllegalStateException e) {
			NPCMain.instance.sendDebugMessage(Level.SEVERE, "Error restoring an NPC: Invalid JSON");
			NPCMain.instance.sendDebugMessage(Level.SEVERE, "The NPC will be removed from storage.");
			NPCMain.instance.storage.removeNPC(name);
			return null;
		}
    }
    
}
