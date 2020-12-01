package io.github.scroojalix.npcmanager.nms.v1_8_R2;

import java.util.List;
import java.util.NoSuchElementException;

import org.bukkit.Bukkit;
import org.bukkit.craftbukkit.v1_8_R2.entity.CraftPlayer;
import org.bukkit.entity.Player;

import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToMessageDecoder;
import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.nms.interfaces.IPacketReader;
import io.github.scroojalix.npcmanager.utils.interactions.InteractAtNPCEvent;
import io.github.scroojalix.npcmanager.utils.npc.NPCData;
import io.github.scroojalix.npcmanager.utils.interactions.InteractAtNPCEvent.NPCAction;
import net.minecraft.server.v1_8_R2.EntityPlayer;
import net.minecraft.server.v1_8_R2.Packet;
import net.minecraft.server.v1_8_R2.PacketPlayInUseEntity;

public class PacketReader extends IPacketReader {
	
	public PacketReader(NPCMain main) {
		super(main);
	}

	public void inject(Player player) {
		try {
			CraftPlayer craftPlayer = (CraftPlayer) player;
			channel = craftPlayer.getHandle().playerConnection.networkManager.k;
			channels.put(player.getUniqueId(), channel);
			
			if (channel.pipeline().get("PacketInjector") != null) return;
			
			channel.pipeline().addAfter("decoder", "PacketInjector", new MessageToMessageDecoder<PacketPlayInUseEntity>() {
	
				@Override
				protected void decode(ChannelHandlerContext channel, PacketPlayInUseEntity packet, List<Object> arg) throws Exception {
					arg.add(packet);
					readPacket(player, packet);
				}
				
			});
		} catch(NoSuchElementException e) {
		}
	}

	public void readPacket(Player player, Packet<?> packet) {
		
		if (packet.getClass().getSimpleName().equalsIgnoreCase("PacketPlayInUseEntity")) {
			
			if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT")) return;
			
			int id = (int) getValue(packet, "a");
			
			if (getValue(packet, "action").toString().equalsIgnoreCase("INTERACT_AT")) {
				if (list.contains(player.getUniqueId())) return;
				for (NPCData npc : main.npc.getNPCs().values()) {
					if (((EntityPlayer) npc.getNPC()).getId() == id) {
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
			}
			if (getValue(packet, "action").toString().equalsIgnoreCase("ATTACK")) {
				for (NPCData npc : main.npc.getNPCs().values()) {
					if (((EntityPlayer) npc.getNPC()).getId() == id) {
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
