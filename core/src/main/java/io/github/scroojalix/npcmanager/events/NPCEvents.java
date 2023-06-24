package io.github.scroojalix.npcmanager.events;

import java.util.logging.Level;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.npc.interactions.InteractAtNPCEvent;

public class NPCEvents implements Listener {

	private NPCMain main;

	public NPCEvents(NPCMain main) {
		this.main = main;
	}
	
	@EventHandler
	public void onNPCInteract(InteractAtNPCEvent event) {
		NPCMain.instance.sendDebugMessage(Level.INFO, String.format("%s occurred: %s", event.getEventName(), event.toString()));
		event.callInteractEventIfDefined();
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		main.npc.sendScoreboardPackets(event.getPlayer());
	}
}
