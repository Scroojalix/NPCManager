package io.github.scroojalix.npcmanager.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.scroojalix.npcmanager.common.interactions.InteractAtNPCEvent;

public class NPCEvents implements Listener {
	
	@EventHandler
	public void onNPCInteract(InteractAtNPCEvent event) {
		if (event.getNPCData().getInteractEvent() != null) event.getNPCData().getInteractEvent().onInteract(event);
	}
}
