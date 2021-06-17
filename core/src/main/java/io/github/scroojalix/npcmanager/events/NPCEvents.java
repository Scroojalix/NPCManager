package io.github.scroojalix.npcmanager.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.common.interactions.InteractAtNPCEvent;

public class NPCEvents implements Listener {
	
	private NPCMain main;
	
	public NPCEvents(NPCMain main) {
		this.main = main;
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent e) {		
		main.reader.inject(e.getPlayer());
	}
	
	@EventHandler
	public void onQuit(PlayerQuitEvent e) {
		main.reader.uninject(e.getPlayer());
	}
	
	@EventHandler
	public void onNPCInteract(InteractAtNPCEvent event) {
		if (event.getNPCData().getInteractEvent() != null) event.getNPCData().getInteractEvent().onInteract(event);
	}
}
