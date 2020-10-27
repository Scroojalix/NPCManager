package me.scroojalix.npcmanager.api;

import me.scroojalix.npcmanager.utils.InteractAtNPCEvent;

public interface InteractEvent {
	
	public String getInteractionName();

	public void entryPoint(InteractAtNPCEvent event);
	
}
