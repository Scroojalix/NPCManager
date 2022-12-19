package io.github.scroojalix.npcmanager.nms.v1_18_R2;

import org.bukkit.Location;
import org.bukkit.craftbukkit.v1_18_R2.CraftWorld;
import org.bukkit.craftbukkit.v1_18_R2.util.CraftChatMessage;

import io.github.scroojalix.npcmanager.nms.interfaces.NMSHologram;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.decoration.ArmorStand;

public class EntityNMSHologram extends ArmorStand implements NMSHologram {

	public EntityNMSHologram(Location loc, String text) {
		super(EntityType.ARMOR_STAND, ((CraftWorld)loc.getWorld()).getHandle());
		setNoGravity(true);
		setCustomName(CraftChatMessage.fromStringOrNull(text));
		setCustomNameVisible(getCustomName() != null && !getCustomName().toString().isEmpty());
		setInvisible(true);
		setNoBasePlate(true);
		setSmall(true);
		setMarker(true);
		setPos(loc.getX(), loc.getY(), loc.getZ());
	}

	public ArmorStand getEntity() {
		return this;
	}
}
