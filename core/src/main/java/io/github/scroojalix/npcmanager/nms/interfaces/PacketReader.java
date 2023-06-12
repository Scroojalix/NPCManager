package io.github.scroojalix.npcmanager.nms.interfaces;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

import com.comphenix.protocol.PacketType;
import com.comphenix.protocol.ProtocolLibrary;
import com.comphenix.protocol.ProtocolManager;
import com.comphenix.protocol.events.ListenerPriority;
import com.comphenix.protocol.events.PacketAdapter;
import com.comphenix.protocol.events.PacketContainer;
import com.comphenix.protocol.events.PacketEvent;
import com.comphenix.protocol.wrappers.EnumWrappers.EntityUseAction;

import io.github.scroojalix.npcmanager.NPCMain;
import io.github.scroojalix.npcmanager.common.PluginUtils;
import io.github.scroojalix.npcmanager.common.interactions.InteractAtNPCEvent;
import io.github.scroojalix.npcmanager.common.interactions.InteractAtNPCEvent.NPCAction;
import io.github.scroojalix.npcmanager.common.npc.NPCData;

public class PacketReader {
	
	private NPCMain main;
	
	private Set<UUID> list = new HashSet<UUID>();

	private ProtocolManager manager;
	
	public PacketReader(NPCMain main) {
		this.main = main;
		this.manager = ProtocolLibrary.getProtocolManager();
	}

	public void registerPacketListener() {
		manager.addPacketListener(new PacketAdapter(main, ListenerPriority.NORMAL,
			PacketType.Play.Client.USE_ENTITY) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				PacketContainer packet = event.getPacket();
				Player p = event.getPlayer();

				// Get values from packet
				int id = packet.getIntegers().read(0);
				EntityUseAction action = packet.getEnumEntityUseActions().read(0).getAction();
				boolean crouched = packet.getBooleans().read(0);

				for (String npcName : PluginUtils.getAllNPCNames()) {
					// Check if entity is NPC created by NPCManager
					if (PluginUtils.getNPCIDByName(npcName) == id) {
						handleInteraction(p, PluginUtils.getNPCDataByName(npcName), action, crouched);
						break;
					}
				}
			}
		});
	}

	public void deregisterPacketListener() {
		manager.removePacketListeners(main);
	}

	private void handleInteraction(Player p, NPCData data, EntityUseAction action, boolean crouched) {
		if (list.contains(p.getUniqueId())) return;
		switch(action) {
			case ATTACK: // Left Click
			// TODO test if this needs to be synchronised
			Bukkit.getPluginManager().callEvent(new InteractAtNPCEvent(p, data, NPCAction.LEFT_CLICK, crouched));
			break;
			case INTERACT_AT: // Right Click
			Bukkit.getPluginManager().callEvent(new InteractAtNPCEvent(p, data, NPCAction.RIGHT_CLICK, crouched));
			list.add(p.getUniqueId());
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {
				@Override
				public void run() {
					list.remove(p.getUniqueId());
				}
			}, 1l);
			
			break;
			case INTERACT: // Extra Right Click (ignored)
			break;
		}
	}
}
