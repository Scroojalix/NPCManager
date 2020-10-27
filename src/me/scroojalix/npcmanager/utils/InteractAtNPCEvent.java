package me.scroojalix.npcmanager.utils;

import org.bukkit.entity.Player;
import org.bukkit.event.Cancellable;
import org.bukkit.event.Event;
import org.bukkit.event.HandlerList;

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
	
	public Player getPlayer() {
		return player;
	}
	
	public NPCData getNPCData() {
		return data;
	}
	
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
}
