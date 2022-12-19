package io.github.scroojalix.npcmanager.nms.v1_18_R1;

import java.util.List;
import java.util.NoSuchElementException;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_18_R1.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.common.interactions.InteractAtNPCEvent;
import io.github.scroojalix.npcmanager.common.interactions.InteractAtNPCEvent.NPCAction;
import io.github.scroojalix.npcmanager.common.npc.NPCData;
import io.github.scroojalix.npcmanager.nms.interfaces.IPacketReader;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import net.minecraft.network.protocol.game.ServerboundInteractPacket;
import net.minecraft.server.level.ServerPlayer;

public class PacketReader extends IPacketReader {

	public PacketReader(NPCMain main) {
		super(main);
	}

	public void inject(Player player) {
		try {
			CraftPlayer craftPlayer = (CraftPlayer) player;
			channel = craftPlayer.getHandle().connection.connection.channel;
			channels.put(player.getUniqueId(), channel);

			if (channel.pipeline().get("PacketInjector") != null) return;
			
			channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<ServerboundInteractPacket>() {
				@Override
				protected void decode(ChannelHandlerContext channel, ServerboundInteractPacket packet, List<Object> arg) throws Exception {
					arg.add(packet);
					readPacket(player, packet);
				}
			});
		} catch(NoSuchElementException e) {
		}
	}
	
	public void readPacket(Player player, ServerboundInteractPacket packet) {
		
		//TODO remove this if statement on all versions. Not necessary.
		// Also improve efficiency of code ie. use else if + store action in String
		// Remove channel from super class. Should be stored per method.
		// See if its possible to remove this class altogether and use a built in event
		if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {			
			
			String action = getValueFromMethod(getValue(packet, "b"), "a").toString();

			if (action.equalsIgnoreCase("INTERACT")) return;
			
			int id = (int) getValue(packet, "a");
			
			if (action.equalsIgnoreCase("INTERACT_AT")) {
				if (list.contains(player.getUniqueId())) return;
				for (NPCData npc : main.npc.getNPCs().values()) {
					if (((ServerPlayer) npc.getNPC()).getId() == id) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {
							@Override
							public void run() {
								Bukkit.getPluginManager().callEvent(new InteractAtNPCEvent(player, npc, NPCAction.RIGHT_CLICK));
							}
						}, 0);
						
						list.add(player.getUniqueId());
						Bukkit.getScheduler().runTaskLater(main, new Runnable() {
							@Override
							public void run() {
								list.remove(player.getUniqueId());
							}
						}, 1l);
					}
				}
			} else if (action.equalsIgnoreCase("ATTACK")) {
				for (NPCData npc : main.npc.getNPCs().values()) {
					if (((ServerPlayer) npc.getNPC()).getId() == id) {
						Bukkit.getScheduler().scheduleSyncDelayedTask(main, new Runnable() {

							@Override
							public void run() {
								Bukkit.getPluginManager().callEvent(new InteractAtNPCEvent(player, npc, NPCAction.LEFT_CLICK));
							}
						}, 0);
					}
				}
			}
		}
	}
}
