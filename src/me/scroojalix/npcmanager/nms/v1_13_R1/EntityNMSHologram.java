package me.scroojalix.npcmanager.nms.v1_13_R1;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_13_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_13_R1.util.CraftChatMessage;

import me.scroojalix.npcmanager.nms.interfaces.NMSHologram;
import net.minecraft.server.v1_13_R1.EntityArmorStand;

public class EntityNMSHologram extends EntityArmorStand implements NMSHologram {

	public EntityNMSHologram(Location loc, String text) {
		super(((CraftWorld)loc.getWorld()).getHandle());
		setNoGravity(true);
		setCustomName(CraftChatMessage.fromStringOrNull(text));
		setCustomNameVisible(getCustomName() != null && !getCustomName().toString().isEmpty());
		setInvisible(true);
		setBasePlate(true);
		setSmall(true);
		setMarker(true);
		setPosition(loc.getX(), loc.getY(), loc.getZ());
	}

	public EntityArmorStand getEntity() {
		return this;
	}
}
