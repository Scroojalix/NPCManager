package me.scroojalix.npcmanager.api;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.Plugin;

import me.scroojalix.npcmanager.NPCMain;

public class InteractionsManager {

	private static Map<String, InteractEvent> interactEvents = new HashMap<String, InteractEvent>();
	
	public static Map<String, InteractEvent> getInteractEvents() {
		return interactEvents;
	}
	
	public static void registerInteraction(InteractEvent interactEvent, Plugin plugin) {
		if (interactEvent.getInteractionName() == null) {
			NPCMain.instance.getLogger().warning("Could not register interaction from "+plugin.getName()+" because it's name is null.");
			return;
		}
		String name = interactEvent.getInteractionName().replace(" ", "-");
		interactEvents.put(name, interactEvent);
	}
}
