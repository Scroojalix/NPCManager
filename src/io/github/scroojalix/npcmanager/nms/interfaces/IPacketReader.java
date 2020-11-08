package io.github.scroojalix.npcmanager.nms.interfaces;

import java.lang.reflect.Field;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import org.bukkit.entity.Player;

import io.netty.channel.Channel;
import io.github.scroojalix.npcmanager.NPCMain;

public abstract class IPacketReader {
	
	protected Channel channel;
	protected Map<UUID, Channel> channels = new HashMap<UUID, Channel>();
	protected NPCMain main;
	
	protected Set<UUID> list = new HashSet<UUID>();
	
	public IPacketReader(NPCMain main) {
		this.main = main;
	}

	/**
	 * Injects a player into the PacketReader so they can interact with NPC's
	 * @param player - The player to inject into the PacketReader
	 */
	public abstract void inject(Player player);
	
	/**
	 * Removes a player from the PacketReader
	 * @param player - The player to remove from the PacketReader
	 */
	public void uninject(Player player) {
		channel = channels.get(player.getUniqueId());
		if (channel.pipeline().get("PacketInjector") != null) {
			channel.pipeline().remove("PacketInjector");
		}
	}
	
	protected Object getValue(Object instance, String name) {
		Object result = null;
		try {
			Field field = instance.getClass().getDeclaredField(name);
			field.setAccessible(true);
			result = field.get(instance);
			field.setAccessible(false);
		}catch(Exception e) {
			e.printStackTrace();
		}
		return result;
	}
}
