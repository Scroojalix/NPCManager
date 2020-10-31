package me.scroojalix.npcmanager.api;

import me.scroojalix.npcmanager.utils.InteractAtNPCEvent;

public interface InteractEvent {
	
	/**
	 * This method returns the name of the Interaction so that the NPC Manager plugin 
	 * can register it correctly and the user can use this Interact Event on an NPC.
	 * <p>
	 * <b>Do not set this value to <i>NULL</i>, otherwise the interact event will not be
	 * registered correctly, and it will be unusable.</b>
	 * @return The name of this Interact Event.
	 */
	public String getInteractionName();

	//TODO put right link in.
	/**
	 * This method will be called when a player interacts with an NPC with this
	 * Interact Event. It can be used to customise your NPC's however you wish.
	 * <p>
	 * Examples and use cases can be found on the <a href="http://google.com">wiki</a>.
	 * @param event Contains information on the event that is called when an NPC is interacted with.
	 */
	public void entryPoint(InteractAtNPCEvent event);
	
}
