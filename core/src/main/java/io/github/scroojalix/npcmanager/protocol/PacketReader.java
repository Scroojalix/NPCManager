package io.github.scroojalix.npcmanager.protocol;

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
import io.github.scroojalix.npcmanager.npc.NPCContainer;
import io.github.scroojalix.npcmanager.npc.interactions.InteractAtNPCEvent;
import io.github.scroojalix.npcmanager.npc.interactions.NPCAction;
import io.github.scroojalix.npcmanager.utils.PluginUtils;

public class PacketReader {
	
	private NPCMain main;
	
	// Hacky way to prevent right click interaction getting fired twice
	// Player gets added to this list, and then removed 1 tick later
	// When handling the interaction, check if the user is in this list first.
	private Set<UUID> recentInteractors = new HashSet<UUID>();

	private ProtocolManager manager;
	
	public PacketReader(NPCMain main) {
		this.main = main;
		this.manager = ProtocolLibrary.getProtocolManager();
	}

	public void registerInteractPacketListener() {
		manager.addPacketListener(new PacketAdapter(main, ListenerPriority.NORMAL,
			PacketType.Play.Client.USE_ENTITY) {
			@Override
			public void onPacketReceiving(PacketEvent event) {
				PacketContainer packet = event.getPacket().deepClone();
				Player p = event.getPlayer();

				// Get values from packet
				int id = packet.getIntegers().read(0);
				EntityUseAction action;
				if (PluginUtils.ServerVersion.v1_17_R1.atOrAbove()) {
					action = packet.getEnumEntityUseActions().read(0).getAction();				
				} else {
					action = packet.getEntityUseActions().read(0);
				}

				// Prior to 1.16, there were no secondary actions
				Boolean secondary = null;
				if (PluginUtils.ServerVersion.v1_16_R1.atOrAbove()) {
					secondary = packet.getBooleans().read(0);
				}

				for (String npcName : PluginUtils.getAllNPCNames()) {
					// Check if entity is NPC created by NPCManager
					if (PluginUtils.getNPCIDByName(npcName) == id) {
						handleInteraction(p, PluginUtils.getNPCContainerByName(npcName), action, secondary);
						break;
					}
				}
			}
		});
	}

	public void deregisterPacketListeners() {
		manager.removePacketListeners(main);
	}

	private void handleInteraction(Player p, NPCContainer data, EntityUseAction action, Boolean secondary) {
		if (recentInteractors.contains(p.getUniqueId())) return;
		switch(action) {
			case ATTACK: // Left Click
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
				InteractAtNPCEvent event = new InteractAtNPCEvent(p, data.getNPCData(), NPCAction.get(true, secondary));
				Bukkit.getPluginManager().callEvent(event);
				if (!event.isCancelled()) {
					if (data.getInteractEvent() != null) {
						data.getInteractEvent().onInteract(event);
					}
				}
			});
			break;
			case INTERACT_AT: // Right Click
			Bukkit.getScheduler().scheduleSyncDelayedTask(main, () -> {
				InteractAtNPCEvent event = new InteractAtNPCEvent(p, data.getNPCData(), NPCAction.get(false, secondary));
				Bukkit.getPluginManager().callEvent(event);
				if (!event.isCancelled()) {
					if (data.getInteractEvent() != null) {
						data.getInteractEvent().onInteract(event);
					}
				}
			});
			recentInteractors.add(p.getUniqueId());
			Bukkit.getScheduler().runTaskLater(main, new Runnable() {
				@Override
				public void run() {
					recentInteractors.remove(p.getUniqueId());
				}
			}, 1l);
			
			break;
			case INTERACT: // Extra Right Click (ignored)
			break;
		}
	}
}
