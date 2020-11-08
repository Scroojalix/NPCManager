package io.github.scroojalix.npcmanager.nms.v1_8_R3;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_8_R3.CraftWorld;

import io.github.scroojalix.npcmanager.nms.interfaces.NMSHologram;
import net.minecraft.server.v1_8_R3.EntityArmorStand;

public class EntityNMSHologram extends EntityArmorStand implements NMSHologram {

	public EntityNMSHologram(Location loc, String text) {
		super(((CraftWorld)loc.getWorld()).getHandle());
		setCustomName(text);
		setCustomNameVisible(getCustomName() != null && !getCustomName().isEmpty());
	    setInvisible(true);
	    setSmall(true);
		setArms(false);
		setGravity(false);
		setBasePlate(true);
		n(true);
		setPosition(loc.getX(), loc.getY(), loc.getZ());
	}

	public EntityArmorStand getEntity() {
		return this;
	}
}
