package io.github.scroojalix.npcmanager.events;

import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.npc.interactions.InteractAtNPCEvent;

public class NPCEvents implements Listener {
	
	@EventHandler
	public void onNPCInteract(InteractAtNPCEvent event) {
		NPCMain.instance.sendDebugMessage(Level.INFO, String.format("%s occurred: %s", event.getEventName(), event.toString()));
		if (event.getNPCData().getInteractEvent() != null) event.getNPCData().getInteractEvent().onInteract(event);
	}
}
