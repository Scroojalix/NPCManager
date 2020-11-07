package me.scroojalix.npcmanager.nms.v1_16_R3;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_16_R3.CraftWorld;
import org.bukkit.craftbukkit.v1_16_R3.util.CraftChatMessage;

import me.scroojalix.npcmanager.nms.interfaces.NMSHologram;
import net.minecraft.server.v1_16_R3.EntityArmorStand;
import net.minecraft.server.v1_16_R3.EntityTypes;

public class EntityNMSHologram extends EntityArmorStand implements NMSHologram {

	public EntityNMSHologram(Location loc, String text) {
		super(EntityTypes.ARMOR_STAND, ((CraftWorld)loc.getWorld()).getHandle());
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
