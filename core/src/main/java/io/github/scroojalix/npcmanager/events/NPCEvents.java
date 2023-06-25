package io.github.scroojalix.npcmanager.events;

import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.npc.interactions.InteractAtNPCEvent;

public class NPCEvents implements Listener {

	private NPCMain main;

	public NPCEvents(NPCMain main) {
		this.main = main;
	}
	
	@EventHandler
	public void onNPCInteract(InteractAtNPCEvent event) {
		main.sendDebugMessage(Level.INFO, String.format("%s occurred: %s", event.getEventName(), event.toString()));
		event.callInteractEventIfDefined();
	}
}
