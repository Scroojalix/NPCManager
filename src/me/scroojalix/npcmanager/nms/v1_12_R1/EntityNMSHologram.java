package me.scroojalix.npcmanager.nms.v1_12_R1;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_12_R1.CraftWorld;
import org.bukkit.craftbukkit.v1_12_R1.util.CraftChatMessage;

import me.scroojalix.npcmanager.nms.interfaces.NMSHologram;
import net.minecraft.server.v1_12_R1.EntityArmorStand;

public class EntityNMSHologram extends EntityArmorStand implements NMSHologram {

	public EntityNMSHologram(Location loc, String text) {
		super(((CraftWorld)loc.getWorld()).getHandle());
		setCustomName(CraftChatMessage.fromString(text).toString());
		setCustomNameVisible(getCustomName() != null && !getCustomName().isEmpty());
	    setInvisible(true);
	    setSmall(true);
	    setArms(false);
	    setNoGravity(true);
	    setBasePlate(true);
	    setMarker(true);
	    this.collides = false;
		setPosition(loc.getX(), loc.getY(), loc.getZ());
	}

	public EntityArmorStand getEntity() {
		return this;
	}
}
