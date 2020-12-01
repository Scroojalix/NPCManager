package io.github.scroojalix.npcmanager.utils.interactions;

import java.util.HashMap;
import java.util.Map;

import org.bukkit.plugin.Plugin;

import io.github.scroojalix.npcmanager.NPCMain;

public class InteractionsManager {

	private static Map<String, InteractEvent> interactEvents = new HashMap<String, InteractEvent>();
	
	public static Map<String, InteractEvent> getInteractEvents() {
		return interactEvents;
	}
	
	/**
	 * Register an {@link InteractEvent} for use on an NPC.
	 * @param interactEvent The {@link InteractEvent} object to register.
	 * @param plugin The plugin that the {@link InteractEvent} is coming from.
	 */
	public static void registerInteraction(InteractEvent interactEvent, Plugin plugin) {
		if (interactEvent.getInteractionName() == null) {
			NPCMain.instance.getLogger().warning("Could not register interaction from "+plugin.getName()+" because it's name is null.");
			return;
		}
		String name = interactEvent.getInteractionName().replace(" ", "-");
		interactEvents.put(name, interactEvent);
	}
}
