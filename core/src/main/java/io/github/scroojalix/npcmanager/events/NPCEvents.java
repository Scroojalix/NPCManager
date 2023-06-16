package io.github.scroojalix.npcmanager.events;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.npc.interactions.InteractAtNPCEvent;

public class NPCEvents implements Listener {
	
	@EventHandler
	public void onNPCInteract(InteractAtNPCEvent event) {
		if (event.getNPCData().getInteractEvent() != null) event.getNPCData().getInteractEvent().onInteract(event);
	}

	@EventHandler
	public void onJoin(PlayerJoinEvent event) {
		// FIXME if another plugin changes the players scoreboard, this gets overwritten.
		event.getPlayer().setScoreboard(NPCMain.instance.npc.getNPCScoreboard());
	}
}
