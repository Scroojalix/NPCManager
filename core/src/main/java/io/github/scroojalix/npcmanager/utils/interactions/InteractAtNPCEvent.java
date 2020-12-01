package io.github.scroojalix.npcmanager.utils.interactions;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

import io.github.scroojalix.npcmanager.utils.npc.NPCData;

public class InteractAtNPCEvent extends Event implements Cancellable{

	private final Player player;
	private final NPCData data;
	private final NPCAction action;
	private boolean isCancelled;
	private static final HandlerList HANDLERS = new HandlerList();
	
	public InteractAtNPCEvent(Player player, NPCData data, NPCAction action) {
		this.player = player;
		this.data = data;
		this.action = action;
	}
	
	/**
	 * @return The player that interacted with the NPC in this event.
	 */
	public Player getPlayer() {
		return player;
	}
	
	/**
	 * @return The data of the NPC involved in this event.
	 */
	public NPCData getNPCData() {
		return data;
	}
	
	/**
	 * @return The type of interaction involved in this event. 
	 * <p>
	 * Can be <b>LEFT_CLICK</b> or <b>RIGHT_CLICK</b>.
	 */
	public NPCAction getAction() {
		return action;
	}
	
	@Override
	public HandlerList getHandlers() {
		return HANDLERS;
	}
	
	public static HandlerList getHandlerList() {
		return HANDLERS;
	}

	@Override
	public boolean isCancelled() {
		return isCancelled;
	}

	@Override
	public void setCancelled(boolean arg0) {
		isCancelled = arg0;
	}

	/**
	 * Enum containing the types of interactions involved in an {@link InteractAtNPCEvent}
	 */
	public enum NPCAction {
		LEFT_CLICK,
		RIGHT_CLICK;
	}
}
