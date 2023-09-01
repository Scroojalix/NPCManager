package io.github.scroojalix.npcmanager.npc.interactions;

import java.util.HashMap;
import java.util.Map;

import javax.annotation.Nonnull;

import org.bukkit.plugin.Plugin;

import io.github.scroojalix.npcmanager.utils.PluginUtils;

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
	public static void registerInteraction(@Nonnull String name, @Nonnull Plugin source, @Nonnull InteractEvent interactEvent) {
		if (PluginUtils.isAlphanumeric(name)) {
			interactEvents.put(name, interactEvent);
			source.getLogger().info("Successfully registered interaction: " + name);
		} else {
			source.getLogger().warning("Could not register interaction. Name must be alphanumeric.");
		}
	}
}
