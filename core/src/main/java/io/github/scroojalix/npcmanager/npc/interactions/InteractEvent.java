package io.github.scroojalix.npcmanager.npc.interactions;

public interface InteractEvent {

	/**
	 * This method will be called when a player interacts with an NPC with this
	 * Interact Event. It can be used to customise your NPC's however you wish.
	 * @param event Contains information on the event that is called when an NPC is interacted with.
	 */
	public abstract void onInteract(InteractAtNPCEvent event);
	
}
